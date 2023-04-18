package org.citience;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.citience.network.NetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CitienceNodeApplication {
    private static final Logger log = LoggerFactory.getLogger(CitienceNodeApplication.class);

    @Autowired
    private ApplicationConfiguration config;

    @Autowired
    private NetworkService networkService;

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(CitienceNodeApplication.class, args);
    }

    @PreDestroy
    public void onShutDown() {
        log.info("--- Closing application gracefully ---");
        networkService.shutdown();
    }
}
