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
    private static final long CONNECTION_TIMEOUT = 60000;

    @Autowired
    private ApplicationConfiguration config;

    @Autowired
    private NetworkService networkService;

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(CitienceNodeApplication.class, args);
    }

    @PostConstruct
    public void startNetwork() throws InterruptedException {

        log.info("--- Starting NetworkManager ---");
        networkService.start();

        final long connectionStartTime = System.currentTimeMillis();

        while (!networkService.isStarted()) {
            if (System.currentTimeMillis() - connectionStartTime >= CONNECTION_TIMEOUT) {
                log.warn("--- No connection could be established shutting application down ---");
                ctx.close();
            }
            log.info("--- Waiting until NetworkManager established a connection. ---");
            Thread.sleep(1000);
        }

        log.info("We are online, let's start our local initial peer.");
        networkService.startLocalNode();

    }

    @PreDestroy
    public void onShutDown() {
        log.info("--- Closing application gracefully ---");
        networkService.shutdown();
    }
}
