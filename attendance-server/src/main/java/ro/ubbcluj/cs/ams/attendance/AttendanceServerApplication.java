package ro.ubbcluj.cs.ams.attendance;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AttendanceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceServerApplication.class, args);
    }

}
