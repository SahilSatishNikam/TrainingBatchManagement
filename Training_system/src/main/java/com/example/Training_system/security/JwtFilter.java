package com.example.Training_system.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Training_system.entity.User;
import com.example.Training_system.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/auth")   // ✅ ADD THIS LINE (CRITICAL)
                || path.startsWith("/ws")
                || path.startsWith("/topic")
                || path.startsWith("/app");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("➡️ REQUEST: " + request.getRequestURI());

        try {
            String authHeader = request.getHeader("Authorization");
            System.out.println("🔐 AUTH HEADER: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("❌ No token found, continuing filter chain");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            System.out.println("🔑 TOKEN: " + token);

            String email = jwtUtil.extractEmail(token);
            System.out.println("📧 EMAIL: " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
                System.out.println("👤 USER FOUND: " + (user != null));

                if (user != null && jwtUtil.validateToken(token)) {

                    String role = "ROLE_" + user.getRole().name().toUpperCase();
                    System.out.println("🎭 ROLE GENERATED: " + role);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority(role))
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ AUTH SUCCESS: " +
                            SecurityContextHolder.getContext().getAuthentication());
                } else {
                    System.out.println("❌ USER NULL OR TOKEN INVALID");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ JWT ERROR: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}