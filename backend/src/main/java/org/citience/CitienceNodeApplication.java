package org.citience;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CitienceNodeApplication {

    @Autowired
    ApplicationConfiguration config;

    public static void main(final String[] args) {
        SpringApplication.run(CitienceNodeApplication.class, args);
    }
}
