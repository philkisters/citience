package org.citience.models.sensors;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * Representation of a single sensor reading.
 * Currently, only double values are supported.
 */
@Entity
@Table(name = "readings")
@Schema(description = "Represents a single sensor reading at a given time with a measured value.")
public class SensorReading {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "sensorId", referencedColumnName = "id", nullable = false)
    @Schema(description = "The sensor that has taken this measurement")
    private Sensor sensor;

    /**
     * Represents the unix timestamp when this value has been measured at the sensor.
     * There are no constraints on this value, it takes the timestamp available from the original sensor.
     * So time fluctuation might be a case.
     */
    @Schema(description = "The unix timestamp at which this measurement was taken.")
    private long timestamp;

    /**
     * Represents the measured value. Currently, it has to be a double value, later on other measurements will be supported.
     */
    @Schema(description = "The value that was measured at the given time, by the given sensor.")
    private double value;

    /**
     * @param sensor
     * @param timestamp The timestamp the reading was taken
     * @param value     The measured value
     */
    public SensorReading(Sensor sensor, long timestamp, double value) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.value = value;
    }

    protected SensorReading() {

    }

    public void setSensor(final Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SensorReading) obj;
        return this.timestamp == that.timestamp &&
                Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, value);
    }

    @Override
    public String toString() {
        return "SensorReading[" +
                "timestamp=" + timestamp + ", " +
                "value=" + value + ']';
    }
}
