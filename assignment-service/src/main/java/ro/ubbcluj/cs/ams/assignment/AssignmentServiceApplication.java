package ro.ubbcluj.cs.ams.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableEurekaClient
//@EnableOAuth2Sso
@EnableTransactionManagement
@SpringBootApplication
//@EnableCircuitBreaker
//@EnableCaching
public class AssignmentServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(AssignmentServiceApplication.class, args);
    }
}
