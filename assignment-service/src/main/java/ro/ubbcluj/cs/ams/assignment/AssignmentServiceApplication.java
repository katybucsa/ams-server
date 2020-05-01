package ro.ubbcluj.cs.ams.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableEurekaClient
@EnableOAuth2Sso
@EnableTransactionManagement
@SpringBootApplication
public class AssignmentServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(AssignmentServiceApplication.class, args);
    }
}
