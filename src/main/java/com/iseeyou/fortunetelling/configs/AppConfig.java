package com.iseeyou.fortunetelling.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;
import java.util.TimeZone;

@Configuration
@EnableAsync
public class AppConfig {
    @Bean
    public LocaleResolver localResolver(@Value("${app.default-locale:vi}") final String defaultLocale,
                                        @Value("${app.default-timezone:Asia/Ho_Chi_Minh}") final String defaultTimezone) {
        AcceptHeaderLocaleResolver localResolver = new AcceptHeaderLocaleResolver();
        localResolver.setDefaultLocale(new Locale.Builder().setLanguage(defaultLocale).build());
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezone));
        return localResolver;
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name}") final String title,
                                 @Value("${spring.application.description}") final String description) {
        return new OpenAPI()
                .info(new Info().title(title).version("1.0").description(description)
                        .termsOfService("https://www.scamweb.com")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
