package com.personaldatastore.application.config;

import com.personaldatastore.application.service.AuthService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppStartupConfig {

    @Bean
    public ApplicationRunner seedDemoUser(AuthService authService) {
        return args -> authService.createDefaultUserIfAbsent();
    }
}
