package ro.ubbcluj.cs.ams.gateway.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Configuration
@ConfigurationProperties(prefix = "auth.url")
@Getter
@Setter
@Validated
public class GatewayProperties {

    private String login;
}

