package org.citience.models.sensors;

import org.citience.models.location.AddressLocation;
import org.citience.models.location.GPSLocation;

public record SensorInfo(String sensorName,
                         AddressLocation address,
                         GPSLocation location) {
}
