package com.tanduydev.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.ConfigurableEnvironment;
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AwsSsmConfig {
    private final ConfigurableEnvironment env;

    @PostConstruct
    public void init() {
        try {
            SsmClient ssmClient = SsmClient.create();
            String dbPass = ssmClient.getParameter(GetParameterRequest.builder()
                    .name("/ecommerce/prod/db-password").withDecryption(true).build()).parameter().value();
            System.setProperty("DB_PASSWORD", dbPass);
        } catch (Exception e) {
            log.error("Unable to connect to AWS SSM to retrieve the password.: {}", e.getMessage());
        }
    }
}