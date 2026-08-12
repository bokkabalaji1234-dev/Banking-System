package com.bank.api_gateway.config;

import com.bank.api_gateway.filter.GatewayLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> accountRoute() {

        return route("account-service-java")
                .GET("/accounts/**", http())
                .before(uri("lb://ACCOUNT-SERVICE"))
                .filter(GatewayLoggingFilter.log())
                .build();
    }
}