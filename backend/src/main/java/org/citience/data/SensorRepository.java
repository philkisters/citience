package org.citience.data;

import org.citience.models.sensors.Sensor;
import org.citience.models.sensors.SensorInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorRepository extends MongoRepository<Sensor, String> {
    Optional<Sensor> findByInfo(SensorInfo info);
}
