package com.example.cersystem.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/events/api/create", "/api/auth/login", "/api/auth/logout", "/events/register/**" )
                )
                .authorizeHttpRequests((requests) -> requests
                        // allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // mustache login/static
                        .requestMatchers("/login", "/events", "/events/api", "/css/**", "/js/**", "/images/**", "/style.css").permitAll()

                        // angular auth api
                        .requestMatchers("/api/auth/**").permitAll()

                        // helpful to avoid redirect weirdness on errors
                        .requestMatchers("/error").permitAll()

                        // Any authenticated role can see the dashboard/profile
                        .requestMatchers("/dashboard").hasAnyRole("STUDENT", "ORGANIZER", "ADMIN")

                        // Story-based examples for future endpoints
                        .requestMatchers(HttpMethod.GET, "/api/events/**", "/events/api").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/events/register/**")
                        .hasRole("STUDENT")

                        .requestMatchers(HttpMethod.POST, "/api/events/**")
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/events/**")
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/events/**")
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers("/api/registrations/**")
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/events/api/create")
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Angular dev server origin
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // allow cookies/session
        config.setAllowCredentials(true);

        // methods used by Angular + preflight
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // include CSRF header
        config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "X-Requested-With", "Accept", "Origin"));

        // optional: expose headers if needed
        config.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}