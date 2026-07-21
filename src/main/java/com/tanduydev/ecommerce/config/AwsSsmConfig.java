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
        // Chỉ chạy SSM nếu cấu hình cho phép hoặc ở môi trường production
        try {
            SsmClient ssmClient = SsmClient.create();
            String dbPass = ssmClient.getParameter(GetParameterRequest.builder()
                    .name("/ecommerce/prod/db-password").withDecryption(true).build()).parameter().value();
            System.setProperty("DB_PASSWORD", dbPass);
            log.info("Successfully retrieved database password from AWS SSM.");
        } catch (Exception e) {
            log.warn("Running in local mode or unable to connect to AWS SSM (Skipping): {}", e.getMessage());
        }
    }
}