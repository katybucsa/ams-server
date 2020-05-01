package ro.ubbcluj.cs.ams.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ComponentScan(basePackages = {"ro.ubbcluj.cs.ams.utils.config"})
@ConfigurationProperties(prefix = "grant-type.password")
@Validated
@Getter
@Setter
public class AuthConfiguration {

    private String user;
    private String pass;
}
