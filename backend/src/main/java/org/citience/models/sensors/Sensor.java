package org.citience.models.sensors;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Sensor {

    @Id
    private String id;

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

    public String getId() {
        return id;
    }

    public void addReading(final SensorReading reading) {
        this.readings.add(reading);
    }
}
