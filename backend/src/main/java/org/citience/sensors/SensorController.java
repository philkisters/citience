package org.citience.sensors;

import org.citience.communication.CommunicationService;
import org.citience.communication.events.SensorCreationEvent;
import org.citience.communication.events.SensorReadingEvent;
import org.citience.data.SensorRepository;
import org.citience.models.sensors.Sensor;
import org.citience.models.sensors.SensorReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final CommunicationService communicationService;
    private final SensorRepository sensorRepository;

    @Autowired
    public SensorController(final CommunicationService communicationService, final SensorRepository sensorRepository) {
        this.communicationService = communicationService;
        this.sensorRepository = sensorRepository;
    }

    @GetMapping("/")
    public Iterable<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @PostMapping("/")
    public Sensor createSensor(@RequestBody Sensor info) {
        var sensorInfo = sensorRepository.save(info);
        communicationService.publishCommunicationEvent(new SensorCreationEvent(info));
        return sensorInfo;
    }

    @PostMapping("/{id}/publish")
    public SensorReading publishSensorReading(@PathVariable(name="id") Long id, @RequestBody SensorReading sensorReading) {
        AtomicReference<SensorReading> result = new AtomicReference<>();
        sensorRepository.findById(id).ifPresentOrElse(sensor -> {
            sensorReading.setSensor(sensor);
            communicationService.publishCommunicationEvent(new SensorReadingEvent(sensorReading));
            result.set(sensorReading);
        }, () -> {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensor not found"
            );
        });

        return result.get();
    }

    @GetMapping("/{id}")
    public Sensor getSensorById(@PathVariable(name="id") Long id) {
        Optional<Sensor> sensor = sensorRepository.findById(id);
        if (sensor.isPresent()) {
            return sensor.get();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensor not found"
            );
        }
    }
}
