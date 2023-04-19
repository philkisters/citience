package org.citience.configuration;

import com.typesafe.config.ConfigFactory;
import org.skabnet.SkABnetConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {
    @Value("${network.timeout}")
    private long timeout;

    public SkABnetConfig getSkABNetConfig() {
        return new SkABnetConfig(ConfigFactory.load("skabnet"));
    }

    public long getTimeout() {
        return timeout;
    }
}
