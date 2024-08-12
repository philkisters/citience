package org.citience.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/network")
public class NetworkController {
    private static final Logger log = LoggerFactory.getLogger(NetworkController.class);

    private final NetworkService networkService;

    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @PostMapping("/start")
    public ResponseEntity<Void> connectToNetwork() {
        log.info("--- Starting NetworkService ---");
        networkService.start().handle((result, exception) -> {
            if (exception != null) {
                networkService.setLastException(exception);
                log.warn("--- No connection could be established shutting down drasyl ---");
                networkService.shutdown();
            }
            return result;
        });

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/reference")
    public ResponseEntity<Void> setReference(@RequestParam String reference) {
        try {
            networkService.setReferenceAddress(reference);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/stop")
    public ResponseEntity<Void> disconnectFromNetwork() {
        networkService.shutdown();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/status")
    public ResponseEntity<NetworkStatus> getNetworkStatus() {
        return new ResponseEntity<>(this.networkService.getStatus(), HttpStatus.ACCEPTED);
    }
}
