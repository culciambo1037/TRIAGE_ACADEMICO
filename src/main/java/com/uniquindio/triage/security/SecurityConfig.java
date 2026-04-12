package com.uniquindio.triage.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Público — solo login
                .requestMatchers("/api/auth/login").permitAll()

                // Autenticación
                .requestMatchers("/api/auth/logout").authenticated()

                // Solicitudes
                .requestMatchers(HttpMethod.POST, "/api/solicitudes")
                    .hasAnyRole("ESTUDIANTE", "RESPONSABLE", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes")
                    .hasAnyRole("RESPONSABLE", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/**")
                    .hasAnyRole("ESTUDIANTE", "RESPONSABLE", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/**/clasificar")
                    .hasAnyRole("RESPONSABLE", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/**/asignar")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/**/estado")
                    .hasAnyRole("RESPONSABLE", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/**/cerrar")
                    .hasAnyRole("RESPONSABLE", "ADMIN")

                // Historial
                .requestMatchers("/api/solicitudes/**/historial")
                    .hasAnyRole("RESPONSABLE", "ADMIN")

                // Usuarios
                .requestMatchers("/api/usuarios/**")
                    .hasRole("ADMIN")

                // IA
                .requestMatchers("/api/ia/**")
                    .hasAnyRole("RESPONSABLE", "ADMIN")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}