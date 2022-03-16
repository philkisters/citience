package org.citience.models.sensors;

/**
 * Representation of a single sensor reading.
 * Currently, only double values are supported.
 *
 * @param timestamp The timestamp the reading was taken
 * @param value The measured value
 */
public record SensorReading(long timestamp, double value) {
}
