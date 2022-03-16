package org.citience.models.sensors;

import java.util.ArrayList;
import java.util.List;

public class Sensor {

    private final SensorInfo info;
    private final List<SensorReading> readings;

    public Sensor(final SensorInfo info) {
        this.info = info;
        this.readings = new ArrayList<>();
    }

    public SensorInfo getInfo() {
        return info;
    }

    public List<SensorReading> getReadings() {
        return List.copyOf(readings);
    }

    public void addReading(final SensorReading reading) {
        this.readings.add(reading);
    }
}
