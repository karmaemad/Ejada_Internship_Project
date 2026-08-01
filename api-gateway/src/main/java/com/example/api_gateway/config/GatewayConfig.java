package com.example.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("wallet-service", r -> r.path("/auth/**", "/wallet/**", "/admin/**")
                        .uri("lb://wallet-service"))
                .route("inventory-service", r -> r.path("/products/**")
                        .uri("lb://inventory-service"))
                .route("shop-service", r -> r.path("/cart/**", "/orders/**")
                        .uri("lb://shop-service"))
                .build();
    }
}