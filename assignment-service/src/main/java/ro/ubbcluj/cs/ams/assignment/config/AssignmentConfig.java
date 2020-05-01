package ro.ubbcluj.cs.ams.assignment.config;

import com.alibaba.cloud.circuitbreaker.sentinel.SentinelCircuitBreakerFactory;
import com.alibaba.cloud.circuitbreaker.sentinel.SentinelConfigBuilder;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.Collections;

@ComponentScan(basePackages = {"ro.ubbcluj.cs.ams.utils.config"})
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

    @Configuration
    public class SentinelAspectConfiguration {

        @Bean
        public SentinelResourceAspect sentinelResourceAspect() {
            return new SentinelResourceAspect();
        }
    }

//    @Bean
//    public Customizer<SentinelCircuitBreakerFactory> defaultConfig() {
//        return factory -> {
//            factory.configureDefault(
//                    id -> new SentinelConfigBuilder().resourceName(id)
//                            .rules(Collections.singletonList(new DegradeRule(id)
//                                    .setGrade(RuleConstant.DEGRADE_GRADE_RT).setCount(100)
//                                    .setTimeWindow(10)))
//                            .build());
//        };
//    }
}
