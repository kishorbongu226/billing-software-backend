package in.kishorbongu.billingsoftware.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import in.kishorbongu.billingsoftware.filter.JwtRequestFilter;
import in.kishorbongu.billingsoftware.service.implementation.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring SecurityFilterChain - starting configuration");

        // Log a short summary of security rules for troubleshooting
        log.debug("Permitting endpoints: /login, /encode");
        log.debug("Role-restricted endpoints: /category, /items -> roles USER or ADMIN");
        log.debug("Admin-only endpoints: /admin/**");

        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/encode", "/actuator/health", "/error").permitAll()
                    .requestMatchers("/actuator/health").permitAll()

                    .requestMatchers("/categories", "/items","/orders","/payments","/dashboard").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("Added JwtRequestFilter before UsernamePasswordAuthenticationFilter");
        log.info("SecurityFilterChain configuration complete");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    

    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> allowedOrigins = List.of("http://localhost:5173","https://*.netlify.app","https://*.onrender.com");

        List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
        List<String> allowedHeaders = List.of("Authorization", "Content-Type");

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowedHeaders(allowedHeaders);
        config.setAllowCredentials(true);

        // Logging the CORS configuration (avoid logging sensitive headers/values)
        log.info("CORS configuration set with allowedOrigins={}, allowedMethods={}, allowedHeaders={}, allowCredentials={}",
                allowedOrigins, allowedMethods, allowedHeaders, config.getAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        log.debug("Registered CORS configuration for /**");
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AppUserDetailsService appUserDetailsService,
            PasswordEncoder passwordEncoder) {

        log.info("Creating AuthenticationManager (ProviderManager) with DaoAuthenticationProvider");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        // avoid logging internal provider state that may reveal sensitive details
        log.debug("DaoAuthenticationProvider created and password encoder set");

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        log.info("AuthenticationManager (ProviderManager) created with {} provider(s)", providerManager.getProviders().size());
        return providerManager;
    }
}
