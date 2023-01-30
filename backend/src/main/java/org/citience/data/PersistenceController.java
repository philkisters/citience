package org.citience.data;

import org.citience.communication.CommunicationService;
import org.citience.communication.events.CommunicationEvent;
import org.citience.communication.events.SensorCreationEvent;
import org.citience.communication.events.SensorReadingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceController {

    private final CommunicationService communicationService;
    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    @Autowired
    public PersistenceController(CommunicationService communicationService, SensorRepository sensorRepository, SensorReadingRepository sensorReadingRepository) {
        this.communicationService = communicationService;
        this.sensorRepository = sensorRepository;
        this.sensorReadingRepository = sensorReadingRepository;

        this.communicationService.subscribeToCommunicationEvent(SensorCreationEvent.EVENT_NAME, this::handleSensorCreation);
        this.communicationService.subscribeToCommunicationEvent(SensorReadingEvent.EVENT_NAME, this::handleNewSensorReading);
    }

    public void handleSensorCreation(CommunicationEvent event) {
        if (event instanceof SensorCreationEvent sensorCreationEvent) {
            sensorRepository.save(sensorCreationEvent.sensor());
        }
    }

    public void handleNewSensorReading(CommunicationEvent event) {
        if (event instanceof SensorReadingEvent sensorReadingEvent) {
            sensorReadingRepository.save(sensorReadingEvent.sensorReading());
        }
    }
}
