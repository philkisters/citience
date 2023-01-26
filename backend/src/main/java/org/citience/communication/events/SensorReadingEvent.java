package org.citience.communication.events;

import org.citience.models.sensors.Sensor;
import org.citience.models.sensors.SensorReading;

/**
 * An event indicating that the sensor described by the sensorInfo published a new reading.
 */
public record SensorReadingEvent(Sensor sensor,
                                 SensorReading sensorReading) implements CommunicationEvent {
    public static final String EVENT_NAME = "sensor-reading";

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
