package app.security;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.DispatcherType;

/*************
 * Configuration de sécurité
 * => GET : authorisé
 * => PUT/POST/DELETE : authentifié
 * => authentification, documentation : authoristé
 * ****************/
@Configuration
@EnableWebSecurity
@Profile("usejwt")
public class JwtWebSecurityConfig {

    protected final Log logger = LogFactory.getLog(getClass());


    @Autowired
    private JwtProvider jwtTokenProvider;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests(config -> {
            config.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();

            config.requestMatchers(
                    "/frontend/**",
                    "/frontend/index.html",
                    "/index.html",
                    "/",
                    "/favicon.ico"
            ).permitAll();
            config.requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
            ).permitAll();

            config.requestMatchers("/auth/**").permitAll();
            config.requestMatchers("/api/persons/reset-password").permitAll();
            config.requestMatchers(HttpMethod.GET, "/api/persons/**", "/api/activities/**").permitAll();
            config.requestMatchers(HttpMethod.POST, "/api/persons/**").authenticated();
            config.requestMatchers(HttpMethod.PUT, "/api/persons/**").authenticated();
            config.requestMatchers(HttpMethod.POST, "/api/activities/**").authenticated();
            config.requestMatchers(HttpMethod.PUT, "/api/activities/**").authenticated();
            config.requestMatchers(HttpMethod.DELETE, "/api/activities/**").authenticated();

            config.anyRequest().denyAll();
        });


        http.exceptionHandling(config -> config.accessDeniedPage("/auth/login"));

        JwtFilter customFilter = new JwtFilter(jwtTokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager myAuthenticationManager(
            @Autowired PasswordEncoder encoder,
            @Autowired UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
