package com.g.g.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.g.g.apigateway.helper.JwtHelper;
import com.g.g.apigateway.adapter.Auth0Adapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
@SuppressWarnings({"squid:S2094"})
public class PostFilterAuthorization extends AbstractGatewayFilterFactory<PostFilterAuthorization.Config> {

    public PostFilterAuthorization() {
        super(PostFilterAuthorization.Config.class);
    }

    private final Map<String, String> permissionsMap = new HashMap<>();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @PostConstruct
    void init() {
        permissionsMap.put("create:user", "/api/user");
        permissionsMap.put("read:user", "/api/user/**");
        permissionsMap.put("create:interrogation", "/api/interrogation");
    }

    @Autowired
    Auth0Adapter userService;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            try {

                // get payload from access id, this has the permissions/roles for the users
                Map<String,Object> payload = JwtHelper.getPayload(exchange.getRequest().getHeaders().get("Authorization").get(0));

                if (hasAuthority(exchange.getRequest(), (ArrayList<String>) payload.get("permissions"))) {

                } else {
                    // https://stackoverflow.com/questions/56298502/global-exception-handling-with-spring-cloud-gateway
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "not allow"));
                }

                LinkedHashMap userResponse = userService.findUser(payload.get("sub").toString());

                ServerHttpRequest request = exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders ->
                                httpHeaders.set("app-metadata", userResponse.get("app_metadata").toString()))
                        .build();

                return chain.filter(exchange.mutate().request(request).build());

            } catch (JsonProcessingException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid token"));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        };
    }

    private boolean hasAuthority(ServerHttpRequest request, ArrayList<String> userPermissions) {

        if (CollectionUtils.isEmpty(userPermissions)) {
            throwNotAuthorizedException();
        }

        for (String userPermission : userPermissions) {
            if (antPathMatcher.match(permissionsMap.get(userPermission), request.getPath().toString())) {
                log.debug("hasAuthority match: {} / {}", permissionsMap.get(userPermission), request.getPath());
                return true;
            }
        }

        return false;
    }

    private void throwNotAuthorizedException() {
        throw new RuntimeException("not authorized");
    }

    public static class Config {

    }
}
