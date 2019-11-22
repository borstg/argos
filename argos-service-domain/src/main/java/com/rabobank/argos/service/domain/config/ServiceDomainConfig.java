package com.rabobank.argos.service.domain.config;

import com.rabobank.argos.domain.signing.SignatureValidator;
import org.springframework.context.annotation.Bean;

public class ServiceDomainConfig {
    @Bean
    public SignatureValidator signatureValidator() {
        return new SignatureValidator();
    }
}
