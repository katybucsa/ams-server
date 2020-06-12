package ro.ubbcluj.cs.ams.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@ComponentScan(basePackages = {"ro.ubbcluj.cs.ams.utils.config"})
@Configuration
@EnableResourceServer
public class GatewayConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private GatewayProperties props;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(props.getLogin(),"/auth/oauth/**").permitAll()
//                .antMatchers(HttpMethod.POST,"/*/actuator/shutdown").hasAuthority("ADMIN")
                .regexMatchers(HttpMethod.POST, "gateway/health\\?.*$", "gateway/present\\?.*$").permitAll()//.access("#oauth2.hasScope('health_mod')")
                .antMatchers(HttpMethod.GET, "gateway/running").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
