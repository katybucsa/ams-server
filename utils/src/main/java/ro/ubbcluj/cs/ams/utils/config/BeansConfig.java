package ro.ubbcluj.cs.ams.utils.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BeansConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder WebClientBuilderBean() {

        return WebClient.builder();
    }
}
