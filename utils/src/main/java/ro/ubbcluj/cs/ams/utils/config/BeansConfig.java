package ro.ubbcluj.cs.ams.utils.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeansConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder WebClientBuilderBean() {

        return WebClient.builder();
    }
}
