package org.citience.data;

import org.citience.models.sensors.Sensor;
import org.citience.models.sensors.SensorReading;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorReadingRepository extends CrudRepository<SensorReading, Long> {
}
