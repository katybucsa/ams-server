package ro.ubbcluj.cs.ams.student;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
@EnableRetry
@EnableCaching
public class StudentServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
