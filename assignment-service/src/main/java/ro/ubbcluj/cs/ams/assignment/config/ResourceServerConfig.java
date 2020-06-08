package ro.ubbcluj.cs.ams.assignment.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class ResourceServerConfig  extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .regexMatchers(HttpMethod.POST, "/health\\?.*$", "/present\\?.*$").permitAll()//.access("#oauth2.hasScope('health_mod')")
                .antMatchers(HttpMethod.POST, "/grades").hasAuthority("PROFESSOR")
                .antMatchers(HttpMethod.GET, "/grades").hasAuthority("STUDENT")
                .antMatchers(HttpMethod.POST,"/actuator/shutdown").hasAuthority("ADMIN")
                .anyRequest().authenticated();
    }
}
