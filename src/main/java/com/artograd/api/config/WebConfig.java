package com.artograd.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author Andrii Borozdykh
 */
@Configuration
@EnableWebMvc
@Profile("lambda")
public class WebConfig {

  /**
   * Create required HandlerMapping, to avoid several default HandlerMapping instances being created
   */
  @Bean
  public HandlerMapping handlerMapping() {
    return new RequestMappingHandlerMapping();
  }

  /**
   * Create required HandlerAdapter, to avoid several default HandlerAdapter instances being created
   */
  @Bean
  public HandlerAdapter handlerAdapter() {
    return new RequestMappingHandlerAdapter();
  }

  /**
   * optimization - avoids creating default exception resolvers; not required as the serverless
   * container handles all exceptions
   *
   * <p>By default, an ExceptionHandlerExceptionResolver is created which creates many dependent
   * object, including an expensive ObjectMapper instance.
   */
  @Bean
  public HandlerExceptionResolver handlerExceptionResolver() {
    return new HandlerExceptionResolver() {

      @Override
      public ModelAndView resolveException(
          HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return null;
      }
    };
  }
}
