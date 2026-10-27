package io.github.mbesoft.bp.teams;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
@SpringBootApplication
@ConfigurationPropertiesScan
public class BpTeamsApplication {
  public static void main(String[] args) { SpringApplication.run(BpTeamsApplication.class, args); }
}
