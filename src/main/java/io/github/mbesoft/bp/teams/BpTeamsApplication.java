package io.github.mbesoft.bp.teams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application entry point, following the standard Spring Initializr layout.
 * No notifications are sent at startup.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BpTeamsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpTeamsApplication.class, args);
    }
}
