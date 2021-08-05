package com.g.g.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.g.g.apigateway.helper.AuthorizationHelper;
import com.g.g.apigateway.helper.JwtHelper;
import com.g.g.apigateway.adapter.Auth0Adapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.g.g.apigateway.helper.Constants.UPDATE_USER;

@Component
@Slf4j
@SuppressWarnings({"squid:S2094"})
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    public AuthorizationFilter() {
        super(AuthorizationFilter.Config.class);
    }

    private final Map<String, String> permissionsMap = new HashMap<>();

    @PostConstruct
    void init() {
        permissionsMap.put("create:user", "/api/v0/users");
        permissionsMap.put("read:user", "/api/v0/users/**");
        permissionsMap.put(UPDATE_USER, "/api/v0/users/**");
        permissionsMap.put("delete:user", "/api/v0/users/**");
        permissionsMap.put("create:role_members", "/api/v0/users/**/roles");

        permissionsMap.put("create:interrogation", "/api/interrogation");
    }

    @Autowired
    Auth0Adapter auth0Adapter;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            try {

                // get payload from access id, this has the permissions/roles for the users
                Map<String,Object> payload = JwtHelper.getPayload(exchange.getRequest().getHeaders().get("Authorization").get(0));

                if (AuthorizationHelper.hasAuthority(exchange.getRequest(), (ArrayList<String>) payload.get("permissions"), permissionsMap)) {

                } else {
                    // https://stackoverflow.com/questions/56298502/global-exception-handling-with-spring-cloud-gateway
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "not allow"));
                }

                LinkedHashMap userResponse = auth0Adapter.findUser(payload.get("sub").toString());

                if (userResponse.get("app_metadata") != null) {
                    ServerHttpRequest request = exchange.getRequest()
                            .mutate()
                            .headers(httpHeaders ->
                                    httpHeaders.set("app-metadata", userResponse.get("app_metadata").toString()))
                            .build();
                    return chain.filter(exchange.mutate().request(request).build());
                }

                return chain.filter(exchange);

            } catch (JsonProcessingException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid token"));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        };
    }

    public static class Config {

    }
}
