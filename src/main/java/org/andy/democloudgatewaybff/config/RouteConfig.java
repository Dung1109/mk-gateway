package org.andy.democloudgatewaybff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.andy.democloudgatewaybff.config.GatewayFilterFunctions.relayTokenIfExists;
import static org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions.dedupeResponseHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class RouteConfig {
//    @Bean
//    public RouterFunction<ServerResponse> gatewayRouterFunctionsAddReqHeader() {
//        return route("secret")
//                .GET("/secret", http("http://localhost:8081/secret"))
//                .filter(tokenRelay())
//                .build();
//    }

//    @Bean
//    public RouterFunction<ServerResponse> gatewayRouterFunctionsAddReqHeader() {
//        return route("secret")
//                .route(path("/resource-server/**"), http("http://localhost:8081"))
//                .before(stripPrefix(1))
//                .filter(tokenRelay())
//                .filter(relayTokenIfExists("messaging-client-authorization-code"))
//                .after(dedupeResponseHeader("Access-Control-Allow-Credentials Access-Control-Allow-Origin"))
//                .build();
//    }

//    @Bean
//    public RouterFunction<ServerResponse> gatewayRouterFunctionsAddReqHeader() {
//        return route("logout")
//                .route(path("/logout"), http("http://localhost:9000"))
//                .filter(tokenRelay())
//                .filter(relayTokenIfExists("messaging-client-authorization-code"))
////                .after(dedupeResponseHeader("Access-Control-Allow-Credentials Access-Control-Allow-Origin"))
//                .build();
//    }
}
