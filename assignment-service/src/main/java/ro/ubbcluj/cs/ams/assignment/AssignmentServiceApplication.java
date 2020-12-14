package ro.ubbcluj.cs.ams.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableEurekaClient
@EnableTransactionManagement
@SpringBootApplication
@EnableCaching
@EnableRetry(proxyTargetClass = true)
public class AssignmentServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(AssignmentServiceApplication.class, args);
    }
}
