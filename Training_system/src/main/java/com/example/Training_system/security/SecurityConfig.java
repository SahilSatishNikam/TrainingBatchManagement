package com.example.Training_system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            // ❌ Unauthorized handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, exx) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("Unauthorized");
                })
            )

            // ❌ Disable default auth
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // ✅ Stateless API
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // public
                .requestMatchers(
                    "/",
                    "/login.html",
                    "/admin_dashboard.html",
                    "/trainer_dashboard.html",
                    "/update_progress.html",
                    "/script.js",
                    "/progress.js",
                    "/style.css",
                    "/favicon.ico",
                    "/**/*.js",
                    "/**/*.css",
                    "/**/*.html",
                    "/**/*.ico"
                ).permitAll()

                // auth + uploads + websocket
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/ws/**", "/ws", "/topic/**", "/app/**").permitAll()

                // 🔥 IMPORTANT: do NOT over-restrict endpoints here
                .requestMatchers("/admin/**").authenticated()
                .requestMatchers("/trainer/**").authenticated()

                .anyRequest().authenticated()
            )

            // JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}