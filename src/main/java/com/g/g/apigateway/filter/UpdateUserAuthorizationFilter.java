package com.g.g.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.g.g.apigateway.adapter.Auth0Adapter;
import com.g.g.apigateway.helper.AuthorizationHelper;
import com.g.g.apigateway.helper.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.g.g.apigateway.helper.Constants.UPDATE_USER;

@Component
@Slf4j
@SuppressWarnings({"squid:S2094"})
public class UpdateUserAuthorizationFilter extends AbstractGatewayFilterFactory<UpdateUserAuthorizationFilter.Config> {

    public UpdateUserAuthorizationFilter() {
        super(UpdateUserAuthorizationFilter.Config.class);
    }

    private final Map<String, String> permissionsMap = new HashMap<>();

    @PostConstruct
    void init() {
        permissionsMap.put(UPDATE_USER, "/api/v0/users/**");
    }

    @Autowired
    Auth0Adapter auth0Adapter;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            try {

                // get payload from access id, this has the permissions/roles for the users
                Map<String, Object> payload = JwtHelper.getPayload(exchange.getRequest().getHeaders().get("Authorization").get(0));

                if (AuthorizationHelper.hasAuthority(exchange.getRequest(), (ArrayList<String>) payload.get("permissions"), permissionsMap)) {

                } else {
                    // https://stackoverflow.com/questions/56298502/global-exception-handling-with-spring-cloud-gateway
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "not allow"));
                }

                // TODO check if it's and admin or is himself
                log.warn("TO-DO TASK PENDING!");

                return chain.filter(exchange);

            } catch (JsonProcessingException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid token"));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        };
    }

    public static class Config {

    }
}
