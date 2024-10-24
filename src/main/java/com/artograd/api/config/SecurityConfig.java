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
   * Returns a SecurityFilterChain object based on the provided HttpSecurity configuration.
   *
   * @param http The HttpSecurity object used for configuring security.
   * @return A SecurityFilterChain object.
   * @throws Exception if an error occurs during the configuration process.
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
