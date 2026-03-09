package com.olivaris.olivaris_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.olivaris.olivaris_app.JwtAuthFilter;
import com.olivaris.olivaris_app.dto.ErrorDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // this can user the @PreAuthorize annotations
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                // Allow all requests to auth endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "ENTITY_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/entity/").hasRole("ADMIN")
                .requestMatchers("/api/entity/{entityId}/**").hasAnyRole("ADMIN", "ENTITY_ADMIN")
                .anyRequest()
                .authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        objectMapper.writeValueAsString(new ErrorDto("No autorizado", e.getMessage(), 401))
                    );
                })
                .accessDeniedHandler((request, response, e) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        objectMapper.writeValueAsString(new ErrorDto("Prohibido", e.getMessage(), 403))
                    );
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
