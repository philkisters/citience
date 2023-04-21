/*
 * Copyright © 2021 Philipp Kisters
 * This source code is licensed under MIT.
 * Full license can found in the "/LICENSE"-File or at https://opensource.org/licenses/MIT
 */
package org.citience.network;

import com.typesafe.config.ConfigFactory;
import org.apache.catalina.Store;
import org.citience.communication.CommunicationService;
import org.citience.communication.events.CommunicationEvent;
import org.citience.communication.events.SensorCreationEvent;
import org.citience.configuration.NodeConfiguration;
import org.citience.data.ConfigurationRepository;
import org.citience.models.Configuration;
import org.citience.models.sensors.Sensor;
import org.drasyl.identity.Identity;
import org.drasyl.identity.IdentityPublicKey;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.*;
import org.skabnet.NetworkAccess;
import org.skabnet.SkABNetPeer;
import org.skabnet.SkABnetNode;
import org.skabnet.attributes.Attribute;
import org.skabnet.attributes.AttributeMap;
import org.skabnet.info.Address;
import org.skabnet.info.PeerInfo;
import org.skabnet.messages.AbstractMessage;
import org.skabnet.messages.NumericIdRoutingWrapper;
import org.skabnet.peer.info.AttributeBasedNameId;
import org.skabnet.peer.info.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class NetworkService implements NetworkAccess {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    // Configuration Parameter
    private static final String REFERENCE_ADDRESS = "referenceAddress";
    private static final String NODE_NAME = "nodeName";
    private static final String DRASYL_PUBLIC_KEY = "drasylPublicKey";
    private static final String DRASYL_PROOF_OF_WORK = "drasylProofOfWork";
    private static final String DRASYL_SECRET_KEY = "drasylPrivateKey";

    private final SkABnetNode skABnetNode;
    private final DrasylNode drasylNode;

    private final NodeConfiguration nodeConfiguration;
    private final CommunicationService communicationService;
    private final ConfigurationRepository configurationRepository;


    private Configuration networkConfig;
    private NetworkStatus currentStatus;
    private Throwable lastException;
    private DrasylAddress referenceAddress;
    private String nodeName;

    @Autowired
    NetworkService(NodeConfiguration nodeConfiguration,
                   CommunicationService communicationService, ConfigurationRepository configurationRepository) throws DrasylException {
        this.nodeConfiguration = nodeConfiguration;
        this.communicationService = communicationService;
        this.configurationRepository = configurationRepository;

        configurationRepository.findFirstByModule("network").ifPresentOrElse(this::setConfiguration, () -> {
            Configuration newConfig = new Configuration("network");
            newConfig.addParameter(REFERENCE_ADDRESS, "");
            newConfig.addParameter(NODE_NAME, "New Node");
            try {
                Identity newIdentity = Identity.generateIdentity();
                newConfig.addParameter(DRASYL_PUBLIC_KEY, newIdentity.getIdentityPublicKey().toString());
                newConfig.addParameter(DRASYL_SECRET_KEY, newIdentity.getIdentitySecretKey().toUnmaskedString());
                newConfig.addParameter(DRASYL_PROOF_OF_WORK, newIdentity.getProofOfWork().toString());

            } catch (IOException e) {
               lastException = e;
            }
            networkConfig = configurationRepository.save(newConfig);
        });

        this.referenceAddress = networkConfig.getParameter(REFERENCE_ADDRESS).isBlank() ? null : new DrasylAddress(IdentityPublicKey.of(networkConfig.getParameter(REFERENCE_ADDRESS)));
        this.nodeName = networkConfig.getParameter(NODE_NAME);
        currentStatus = NetworkStatus.OFFLINE;

        Identity drasylIdentity = Identity.of(
                Integer.parseInt(networkConfig.getParameter(DRASYL_PROOF_OF_WORK)),
                networkConfig.getParameter(DRASYL_PUBLIC_KEY),
                networkConfig.getParameter(DRASYL_SECRET_KEY));

        final DrasylConfig drasylConfig =  DrasylConfig.newBuilder(DrasylConfig.of(ConfigFactory.load("drasyl"))).identity(drasylIdentity).build();

        drasylNode = new DrasylNode(drasylConfig) {
            @Override
            public void onEvent(final Event event) {
                if (event instanceof MessageEvent) {
                    final AbstractMessage message = (AbstractMessage) ((MessageEvent) event).getPayload();
                    log.info("{}: received message {} from {}", message.getRecipient().getNameId(), message.getMessageName(), message.getSender().getNameId());

                    if (message.getMessageName().equals(NumericIdRoutingWrapper.MESSAGENAME)) {
                        log.info("Message info: {}", message);
                    }
                    skABnetNode.handleMessage(message);
                }
                else if (event instanceof NodeOnlineEvent) {
                    if (currentStatus == NetworkStatus.DISCONNECTED) {
                        log.warn("Back online! Node has gone online again.");
                    }
                    else {
                        log.info("We are online. Let's go!");
                    }
                    currentStatus = NetworkStatus.ONLINE;
                }
                else if (event instanceof NodeOfflineEvent) {
                    log.warn("Node has gone offline, no messages can be received or sent.");
                    currentStatus = NetworkStatus.DISCONNECTED;
                }
                else if (event instanceof final InboundExceptionEvent exceptionEvent) {
                    log.warn("Received ExceptionEvent:", exceptionEvent.getError());
                }
            }
        };

        skABnetNode = new SkABnetNode(this, this.nodeConfiguration.getSkABNetConfig(), NodeId.fromId(this.nodeName));
    }

    private void setConfiguration(Configuration configuration) {
        this.networkConfig = configuration;
    }

    public CompletionStage<Void> start() {
        if (currentStatus != NetworkStatus.CONNECTING && currentStatus != NetworkStatus.ONLINE) {
            this.currentStatus = NetworkStatus.CONNECTING;
            communicationService.subscribeToCommunicationEvent(SensorCreationEvent.EVENT_NAME, this::startSensorPeer);
            return this.drasylNode.start();
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> send(final Address destination,
                                      final AbstractMessage message) {
        log.info("{}: sending message {} to {}", message.getSender().getNameId(), message.getMessageName(), message.getRecipient().getNameId());
        if (destination instanceof DrasylAddress address) {
            return drasylNode.send(address.address(), message);
        }

        throw new IllegalArgumentException("Address must be of type DrasylAddress");
    }


    public Address getAddress() {
        return new DrasylAddress(drasylNode.identity().getIdentityPublicKey());
    }

    public NetworkStatus getStatus() {
        return currentStatus;
    }

    public DrasylAddress getReferenceAddress() {
        return referenceAddress;
    }

    public void setReferenceAddress(String referenceAddress) {
        this.referenceAddress = new DrasylAddress(IdentityPublicKey.of(referenceAddress));
        networkConfig.addParameter(REFERENCE_ADDRESS, referenceAddress);
        configurationRepository.save(networkConfig);
    }

    public void startLocalNode() {

        PeerInfo localPeerInfo = PeerInfo.fromNameIdAndAddress(AttributeBasedNameId.of("nodeID", this.nodeName), new DrasylAddress(drasylNode.identity().getIdentityPublicKey()));
        final SkABNetPeer localPeer = SkABNetPeer.fromLocalInfoAndRingAmountAndLeafSizeAndSkABNetNode(localPeerInfo, 160, 4, skABnetNode);

        skABnetNode.startPeer(localPeer, referenceAddress);
    }

    public void startSensorPeer(final CommunicationEvent event) {
        if (event instanceof SensorCreationEvent sensorCreationEvent) {
            Sensor info = sensorCreationEvent.sensor();

            AttributeMap attributeMap = skABnetNode.getAttributeMap();
            attributeMap.setValueForAttribute(info.getName(), Attribute.of("sensorID"));
            attributeMap.setValueForAttribute(info.getType(), Attribute.of("type"));
            attributeMap.setValueForAttribute(this.nodeName, Attribute.of("nodeID"));

            skABnetNode.put(attributeMap, getAddress());
        }
    }

    public void shutdown() {
        skABnetNode.shutdown();
        drasylNode.shutdown().toCompletableFuture().join();
    }

    public long getTimeout() {
        return nodeConfiguration.getTimeout();
    }

    public Throwable getLastException() {
        return lastException;
    }

    public void setLastException(Throwable lastException) {
        this.lastException = lastException;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
        networkConfig.addParameter(NODE_NAME, nodeName);
        configurationRepository.save(networkConfig);
    }
}
