package com.nexusenroll.auth.config;

import com.nexusenroll.auth.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures stateless JWT-based security for auth-service: registers the
 * {@link JwtAuthenticationFilter} ahead of the standard login filter, opens
 * login/register/roles/actuator endpoints, restricts user management and staff
 * provisioning to ADMIN, and returns JSON 401/403 bodies instead of the default
 * redirect/HTML responses.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"statusCode\":401,\"message\":\"Unauthorized: " + authException.getMessage() + "\",\"data\":null}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"statusCode\":403,\"message\":\"Forbidden: " + accessDeniedException.getMessage() + "\",\"data\":null}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/api/auth/login"),
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/api/auth/register"),
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/api/auth/roles"),
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/h2-console/**"),
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/actuator/**")
                        ).permitAll()
                        .requestMatchers(
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/api/auth/users/**"),
                                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/api/auth/provision-staff")
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
