package com.artograd.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Creates a SecurityFilterChain for the given HttpSecurity object.
   *
   * @param http the HttpSecurity object to create the SecurityFilterChain for
   * @return the SecurityFilterChain that is built from the provided HttpSecurity object
   * @throws Exception if an error occurs during the creation of the SecurityFilterChain
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
        .oauth2ResourceServer(
            oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
        .csrf(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
