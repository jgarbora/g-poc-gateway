package com.g.g.apigateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
/*
@Configuration
@EnableWebFlux
public class CORSConfiguration  implements WebFluxConfigurer {

   /** @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("PUT","OPTIONS","GET","POST","PATCH")
                .maxAge(3600);
    }*/

    //https://stackoverflow.com/questions/61909640/how-to-disable-cors-in-spring-cloud-gateway

/* @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders(HttpHeaders.SET_COOKIE);
    }


    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("Baeldung-Allowed");

        PathPatternParser parse = new PathPatternParser();
        CorsConfigurationSource source = new UrlBasedCorsConfigurationSource(parse);

        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}*/

