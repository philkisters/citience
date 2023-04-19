package org.citience.data;

import org.citience.models.Configuration;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfigurationRepository  extends CrudRepository<Configuration, Long> {
    Optional<Configuration> findFirstByModule(final String module);
}
