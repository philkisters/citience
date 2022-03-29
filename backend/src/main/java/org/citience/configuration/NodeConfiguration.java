package org.citience.configuration;

import com.typesafe.config.ConfigFactory;
import org.skabnet.SkABnetConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {
    @Value("${network.reference}")
    private String referenceAddress;
    @Value("${node.id}")
    private String nodeId;

    public SkABnetConfig getSkABNetConfig() {
        return new SkABnetConfig(ConfigFactory.load("skabnet"));
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getReferenceAddress() {
        return referenceAddress;
    }
}
