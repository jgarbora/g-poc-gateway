package com.g.g.apigateway.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;


@Configuration
public class SecurityConfiguration {

    @Value( "${spring.security.oauth2.resourceserver.jwt.audience}" )
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;


    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .authorizeExchange()
                .pathMatchers("/api/public").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        /*
        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
        indeed intended for our app. Adding our own validator is easy to do:
        */

        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

/**
 @Bean public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
 //@formatter:off
 return builder.routes()
 .route("path_route", r -> r.path("/get")
 .uri("http://httpbin.org"))

 .route("host_route", r -> r.host("*.myhost.org")
 .uri("http://httpbin.org"))

 .route("rewrite_route", r -> r.host("*.rewrite.org")
 .filters(f -> f.rewritePath("/foo/(?<segment>.*)",
 "/${segment}"))
 .uri("http://httpbin.org"))

 .route("hystrix_route", r -> r.host("*.hystrix.org")
 .filters(f -> f.hystrix(c -> c.setName("slowcmd")))
 .uri("http://httpbin.org"))

 .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
 .filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
 .uri("http://httpbin.org"))

 .route("limit_route", r -> r
 .host("*.limited.org").and().path("/anything/**")
 .filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
 .uri("http://httpbin.org"))

 .route("websocket_route", r -> r.path("/echo")
 .uri("ws://localhost:9000"))

 .build();
 //@formatter:on
 }
 */
}
