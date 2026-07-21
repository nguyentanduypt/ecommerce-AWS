package com.tanduydev.ecommerce.config;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class JacksonConfig {
    @Bean
    public SimpleModule uuidModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UUID.class, new JsonDeserializer<UUID>() {
            @Override
            public UUID deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                return UUID.fromString(value);
            }
        });
        return module;
    }
}