package com.agms.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AppConfig
 * ==========
 * General application bean configurations.
 *
 * Provides:
 *   - ObjectMapper : shared JSON serializer/deserializer
 *                    (used by JwtAuthenticationFilter to write error responses)
 */
@Configuration
public class AppConfig {

    /**
     * Configured ObjectMapper with:
     *   - JavaTimeModule  : supports LocalDateTime, LocalDate etc. in JSON
     *   - No timestamp dates (uses ISO-8601 string format)
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}