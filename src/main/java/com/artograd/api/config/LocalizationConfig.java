package com.artograd.api.config;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocalizationConfig {

  /**
   * Configures the {@link MessageSource} bean for internationalization.
   * 
   * @return the configured {@link ResourceBundleMessageSource} instance.
   */
  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("locales/subjects");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  /**
   * Configures the {@link LocaleResolver} bean to determine the locale
   * based on the Accept-Language header in HTTP requests.
   * 
   * @return the configured {@link AcceptHeaderLocaleResolver} instance.
   */
  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    localeResolver.setDefaultLocale(Locale.US);
    return localeResolver;
  }
}
