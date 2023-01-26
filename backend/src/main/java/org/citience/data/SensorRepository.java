package org.citience.data;

import org.citience.models.sensors.Sensor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository holds all information regarding sensors known by the node.
 */
@Repository
public interface SensorRepository extends CrudRepository<Sensor, Long> {

}
