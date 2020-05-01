package ro.ubbcluj.cs.ams.utils.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Instant;

@Component
public class InstantConfig {

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public Instant instant() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return inst();
        } else {
            final Instant existInstant = (Instant) requestAttributes.getAttribute("actionInstant", RequestAttributes.SCOPE_REQUEST);
            if (existInstant == null) {
                final Instant newInst = inst();
                requestAttributes.setAttribute("actionInstant", newInst, RequestAttributes.SCOPE_REQUEST);
                return newInst;
            }
            return existInstant;
        }
    }

    public Instant inst() {

        return Instant.now();
    }
}
