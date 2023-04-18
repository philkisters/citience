/*
 * Copyright © 2021 Philipp Kisters
 * This source code is licensed under MIT.
 * Full license can found in the "/LICENSE"-File or at https://opensource.org/licenses/MIT
 */
package org.citience.network;

import com.typesafe.config.ConfigFactory;
import org.citience.communication.CommunicationService;
import org.citience.communication.events.CommunicationEvent;
import org.citience.communication.events.SensorCreationEvent;
import org.citience.configuration.NodeConfiguration;
import org.citience.models.sensors.Sensor;
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

import javax.management.ObjectName;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

@Service
public class NetworkService implements NetworkAccess {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);

    private final SkABnetNode skABnetNode;
    private final DrasylNode drasylNode;

    private final NodeConfiguration nodeConfiguration;
    private final CommunicationService communicationService;

    private NetworkStatus currentStatus;
    private Throwable lastException;

    private DrasylAddress referenceAddress;

    @Autowired
    NetworkService(NodeConfiguration nodeConfiguration,
                   CommunicationService communicationService) throws DrasylException {
        this.nodeConfiguration = nodeConfiguration;
        this.communicationService = communicationService;

        this.referenceAddress = nodeConfiguration.getReferenceAddress().isBlank() ? null : new DrasylAddress(IdentityPublicKey.of(nodeConfiguration.getReferenceAddress()));

        currentStatus = NetworkStatus.OFFLINE;

        final DrasylConfig drasylConfig = DrasylConfig.newBuilder( DrasylConfig.of(ConfigFactory.load("drasyl")))
                .identityPath(Paths.get(this.nodeConfiguration.getIdentityPath(), this.nodeConfiguration.getNodeId() + ".identity.json").toAbsolutePath())
                .build();

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

        skABnetNode = new SkABnetNode(this, this.nodeConfiguration.getSkABNetConfig(), NodeId.fromId(this.nodeConfiguration.getNodeId()));
    }

    public CompletionStage<Void> start() {
        communicationService.subscribeToCommunicationEvent(SensorCreationEvent.EVENT_NAME, this::startSensorPeer);
        return this.drasylNode.start();
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
    }

    public void startLocalNode() {

        PeerInfo localPeerInfo = PeerInfo.fromNameIdAndAddress(AttributeBasedNameId.of("nodeID", nodeConfiguration.getNodeId()), new DrasylAddress(drasylNode.identity().getIdentityPublicKey()));
        final SkABNetPeer localPeer = SkABNetPeer.fromLocalInfoAndRingAmountAndLeafSizeAndSkABNetNode(localPeerInfo, 160, 4, skABnetNode);

        skABnetNode.startPeer(localPeer, referenceAddress);
    }

    public void startSensorPeer(final CommunicationEvent event) {
        if (event instanceof SensorCreationEvent sensorCreationEvent) {
            Sensor info = sensorCreationEvent.sensor();

            AttributeMap attributeMap = skABnetNode.getAttributeMap();
            attributeMap.setValueForAttribute(info.getName(), Attribute.of("sensorID"));
            attributeMap.setValueForAttribute(info.getType(), Attribute.of("type"));
            attributeMap.setValueForAttribute(nodeConfiguration.getNodeId(), Attribute.of("nodeID"));

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
}
