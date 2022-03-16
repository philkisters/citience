package org.citience.sensors;

import org.citience.communication.CommunicationService;
import org.citience.models.sensors.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SensorController {
    private final CommunicationService communicationService;

    @Autowired
    public SensorController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @GetMapping("/")
    public List<Sensor> getAllSensors() {
        return new ArrayList<>();
    }
}
