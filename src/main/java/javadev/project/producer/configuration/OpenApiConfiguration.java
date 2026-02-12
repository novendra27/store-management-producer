package javadev.project.producer.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API documentation")
                        .description("API documentation")
                        .version("v1.0.0"));
    }

    @Bean
    public GroupedOpenApi productManagementApi() {
        return GroupedOpenApi.builder()
                .group("Product Management")
                .pathsToMatch("/api/v1/product/**", "/api/v1/products/**")
                .packagesToScan("javadev.project.producer.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi supplierManagementApi() {
        return GroupedOpenApi.builder()
                .group("Supplier Management")
                .pathsToMatch("/api/v1/supplier/**", "/api/v1/suppliers/**")
                .packagesToScan("javadev.project.producer.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryManagementApi() {
        return GroupedOpenApi.builder()
                .group("Category Management")
                .pathsToMatch("/api/v1/category/**", "/api/v1/categories/**")
                .packagesToScan("javadev.project.producer.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi reportManagementApi() {
        return GroupedOpenApi.builder()
                .group("Report Management")
                .pathsToMatch("/api/v1/report/**")
                .packagesToScan("javadev.project.producer.controller")
                .build();
    }
}
