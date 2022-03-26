package org.citience.sensors;

import org.citience.communication.CommunicationService;
import org.citience.communication.events.SensorCreationEvent;
import org.citience.communication.events.SensorReadingEvent;
import org.citience.data.SensorRepository;
import org.citience.models.sensors.Sensor;
import org.citience.models.sensors.SensorInfo;
import org.citience.models.sensors.SensorReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        communicationService.publishCommunicationEvent(new SensorCreationEvent(info));
        return sensor;
    }

    @PostMapping("/{id}/publish")
    public SensorReading publishSensorReading(@PathVariable(name="id") String id, @RequestBody SensorReading sensorReading) {
        AtomicReference<SensorReading> result = new AtomicReference<>();
        sensorRepository.findById(id).ifPresentOrElse((sensor) -> {
            communicationService.publishCommunicationEvent(new SensorReadingEvent(sensor.getInfo(), sensorReading));
            result.set(sensorReading);
        }, () -> result.set(null));

        return result.get();
    }
}
