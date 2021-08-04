package com.g.g.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@SuppressWarnings({"squid:S2094"})
public class AddCorsFilter extends AbstractGatewayFilterFactory<AddCorsFilter.Config> {

    public AddCorsFilter() {
        super(AddCorsFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.debug("adding cors");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*")));
        };

    }

    public static class Config {

    }
}
