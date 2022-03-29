/*
 * Copyright © 2021 Philipp Kisters
 * This source code is licensed under MIT.
 * Full license can found in the "/LICENSE"-File or at https://opensource.org/licenses/MIT
 */
package org.citience.network;

import com.typesafe.config.ConfigFactory;
import org.citience.configuration.NodeConfiguration;
import org.drasyl.identity.IdentityPublicKey;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.*;
import org.skabnet.NetworkAccess;
import org.skabnet.SkABNetPeer;
import org.skabnet.SkABnetNode;
import org.skabnet.info.Address;
import org.skabnet.info.PeerInfo;
import org.skabnet.messages.AbstractMessage;
import org.skabnet.peer.info.AttributeBasedNameId;
import org.skabnet.peer.info.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

@Service
public class NetworkService implements NetworkAccess {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);

    private final SkABnetNode skABnetNode;
    private final DrasylNode drasylNode;

    NodeConfiguration nodeConfig;

    private boolean started;

    @Autowired
    NetworkService(NodeConfiguration nodeConfiguration) throws DrasylException {
        nodeConfig = nodeConfiguration;
        started = false;

        final DrasylConfig drasylConfig = DrasylConfig.newBuilder( DrasylConfig.of(ConfigFactory.load("drasyl")))
                .identityPath(Paths.get("backend","src","main","resources", nodeConfig.getNodeId() + ".identity.json").toAbsolutePath())
                .build();

        drasylNode = new DrasylNode(drasylConfig) {
            @Override
            public void onEvent(final Event event) {
                if (event instanceof MessageEvent) {
                    final AbstractMessage message = (AbstractMessage) ((MessageEvent) event).getPayload();
                    log.info("{}: received message {} from {}", message.getRecipient().getNameId(), message.getMessageName(), message.getSender().getAddress());

                    skABnetNode.handleMessage(message);
                }
                else if (event instanceof NodeOnlineEvent) {
                    if (started) {
                        log.warn("Back online! Peer has gone online again.");
                    }
                    else {
                        started = true;
                        log.info("We are online. Let's go!");
                    }
                }
                else if (event instanceof NodeOfflineEvent) {
                    log.warn("Peer has gone offline, no messages can be received or sent.");
                }
                else if (event instanceof final InboundExceptionEvent exceptionEvent) {
                    log.warn("Received ExceptionEvent:", exceptionEvent.getError());
                }
            }
        };

        skABnetNode = new SkABnetNode(this, nodeConfig.getSkABNetConfig(), NodeId.fromId(nodeConfig.getNodeId()));
    }

    public CompletionStage<Void> start() {
        return this.drasylNode.start();
    }

    @Override
    public CompletionStage<Void> send(final Address destination,
                                      final AbstractMessage message) {
        log.info("{}: sending message {} to {}", message.getSender().getNameId(), message.getMessageName(), destination);
        if (destination instanceof DrasylAddress) {
            final DrasylAddress address = (DrasylAddress) destination;
            return drasylNode.send(address.getDrasylAddress(), message);
        }

        throw new IllegalArgumentException("Address must be of type DrasylAddress");
    }


    public Address getAddress() {
        return new DrasylAddress(drasylNode.identity().getIdentityPublicKey());
    }

    public boolean isStarted() {
        return started;
    }

    public void startLocalNode() {

        PeerInfo localPeerInfo = PeerInfo.fromNameIdAndAddress(AttributeBasedNameId.of("nodeID", nodeConfig.getNodeId()), new DrasylAddress(drasylNode.identity().getIdentityPublicKey()));
        final SkABNetPeer localPeer = SkABNetPeer.fromLocalInfoAndRingAmountAndLeafSizeAndSkABNetNode(localPeerInfo, 160, 4, skABnetNode);

        if (!nodeConfig.getReferenceAddress().isBlank()) {
            log.info("--- Joining existing Network ---");
            skABnetNode.startPeer(localPeer, new DrasylAddress(IdentityPublicKey.of(nodeConfig.getReferenceAddress())));
        } else {
            log.info("--- Creating new Network ---");
            skABnetNode.startPeer(localPeer, null);
        }
    }

    public void shutdown() {
        skABnetNode.shutdown();
        drasylNode.shutdown().toCompletableFuture().join();
    }
}
