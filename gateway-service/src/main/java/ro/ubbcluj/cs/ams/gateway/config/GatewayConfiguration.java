package ro.ubbcluj.cs.ams.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import ro.ubbcluj.cs.ams.gateway.exception.AuthAccessDeniedHandler;
import ro.ubbcluj.cs.ams.gateway.exception.AuthEntryPoint;

@Configuration
@EnableResourceServer
public class GatewayConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private GatewayProperties props;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf()
                .disable()
                .logout().disable()
                .formLogin().disable()
                .cors().and()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers(props.getLogin()).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler()).authenticationEntryPoint(authenticationEntryPoint());
    }

    @Bean
    AuthAccessDeniedHandler accessDeniedHandler() {

        return new AuthAccessDeniedHandler();
    }

    @Bean
    AuthEntryPoint authenticationEntryPoint() {

        return new AuthEntryPoint();
    }
}
