package org.citience.communication.events;

import org.citience.models.sensors.SensorInfo;

public record SensorCreationEvent(
        SensorInfo sensorInfo) implements CommunicationEvent {
    public static final String EVENT_NAME = "sensor-creation";

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
