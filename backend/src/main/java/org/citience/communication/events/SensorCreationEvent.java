package org.citience.communication.events;

import org.citience.models.sensors.Sensor;

public record SensorCreationEvent(
        Sensor sensor) implements CommunicationEvent {
    public static final String EVENT_NAME = "sensor-creation";

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
