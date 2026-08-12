package com.bank.api_gateway.filter;

import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

public class GatewayLoggingFilter {
    public static HandlerFilterFunction<ServerResponse,ServerResponse> log(){
        return (request, next) -> {
            System.out.println("========== GATEWAY REQUEST ==========");
            System.out.println("HTTP Method : " + request.method());
            System.out.println("Request URI : " + request.uri());

            ServerResponse response = next.handle(request);

            System.out.println("HTTP Status : " + response.statusCode());
            System.out.println("=====================================");

            return response;

        };
    }
}
