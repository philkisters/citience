package org.citience.sensors;

import org.citience.communication.CommunicationService;
import org.citience.data.SensorRepository;
import org.citience.models.sensors.Sensor;
import org.citience.models.sensors.SensorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SensorController {
    private final CommunicationService communicationService;
    private final SensorRepository sensorRepository;

    @Autowired
    public SensorController(final CommunicationService communicationService, final SensorRepository sensorRepository) {
        this.communicationService = communicationService;
        this.sensorRepository = sensorRepository;
    }

    @GetMapping("/")
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @PostMapping("/")
    public Sensor createSensor(@RequestBody SensorInfo info) {
        Sensor sensor = new Sensor(info);
        sensor = sensorRepository.save(sensor);
        return sensor;
    }
}
