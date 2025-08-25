package com.democode.todo.config;


import com.democode.todo.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users").permitAll() // Allow user creation (public registration)
                        .anyRequest().authenticated() // Secure everything else
                )
                .httpBasic(Customizer.withDefaults()) // ✅ new recommended way
                .csrf(csrf -> csrf.disable()); // often disabled for APIs

        return http.build();
    }
}