package com.clevora.clevora.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration - Cấu hình bảo mật cho toàn app.
 *
 * Quy tắc:
 *   - PUBLIC endpoints: auth, manga list, genre list, chapter pages...
 *   - USER endpoints: like, follow, history, submit manga...
 *   - ADMIN endpoints: CRUD manga/chapter/genre, approve, user management...
 *   - SUPERADMIN endpoints: change user role
 *   - Stateless session (JWT-based, không dùng cookie session)
 *   - CSRF disabled (API thuần, không form-based)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (REST API stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Exception handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Stateless session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint permissions
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/mangas/*/like").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/mangas/*/like-status").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/mangas/*/follow").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/mangas/*/follow-status").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/mangas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/chapters/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/chapters/*/view").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/genres/**").permitAll()

                        // ADMIN
                        .requestMatchers("/v1/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")

                        // SUPERADMIN
                        .requestMatchers("/v1/superadmin/**").hasRole("SUPERADMIN")

                        // Còn lại: phải đăng nhập
                        .anyRequest().authenticated()
                )

                // JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
