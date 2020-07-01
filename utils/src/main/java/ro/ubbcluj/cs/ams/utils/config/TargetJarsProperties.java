package ro.ubbcluj.cs.ams.utils.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "java.jar.target")
@Getter
@Setter
public class TargetJarsProperties {

    public String assignment_service;
    public String auth_service;
    public String course_service;
    public String gateway_service;
    public String notification_service;
    public String student_service;
}
