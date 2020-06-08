package ro.ubbcluj.cs.ams.utils.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "eureka.registered.services")
@Getter
@Setter
public class EurekaServicesProperties {

    private int number;
}
