package com.example.tistory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        return http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/","/login").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/my/**").hasAnyRole("ADMIN","USER")
                        .anyRequest().authenticated())
                .formLogin((auth)->auth
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProcess")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .csrf((auth)->auth.disable())
                .build();
    }
}
