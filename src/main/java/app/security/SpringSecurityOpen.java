package app.security;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*******Pour les tests unitaires ********/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile("open")
public class SpringSecurityOpen {

    protected final Log logger = LogFactory.getLog(getClass());

    public SpringSecurityOpen() {
        logger.info("+++ SPRING SECURITY OPEN");
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(config -> {
            config.anyRequest().permitAll();
        });
        // no CSRF check
        http.csrf(config -> {
            config.disable();
        });
        return http.build();
    }

}