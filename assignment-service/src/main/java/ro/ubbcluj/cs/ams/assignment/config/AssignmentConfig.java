package ro.ubbcluj.cs.ams.assignment.config;


//import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@ComponentScan(basePackages = {"ro.ubbcluj.cs.ams.utils.config"})
//@EnableRetry
@Configuration
public class AssignmentConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration
                .set(new DefaultExecuteListenerProvider(new DefaultExecuteListener()));

        return jooqConfiguration;
    }
//
//    @Bean
//    public SentinelResourceAspect sentinelResourceAspect() {
//        return new SentinelResourceAspect();
//    }

//    @Bean
//    @Conditional(AllServicesAreRegistered.class)
//    public ServicesHealthChecker servicesHealthChecker(){
//
//        return new ServicesHealthChecker();
//    }

//    @Bean
//    @ConfigurationProperties("example.oauth2.client")
//    protected ClientCredentialsResourceDetails oAuthDetails() {
//        return new ClientCredentialsResourceDetails();
//    }
//    @Bean
//    protected RestTemplate restTemplate() {
//        return new OAuth2RestTemplate(oAuthDetails());
//    }

}
