package com.g.g.apigateway.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class AuthorizationHelper {

    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static boolean hasAuthority(ServerHttpRequest request, ArrayList<String> userPermissions, Map<String, String> permissionsMap) {

        if (CollectionUtils.isEmpty(userPermissions)) {
            log.debug("user permissions is empty");
            return false;
        }

        for (String userPermission : userPermissions) {
            if (permissionsMap.containsKey(userPermission) && antPathMatcher.match(permissionsMap.get(userPermission), request.getPath().toString())) {
                log.debug("hasAuthority match: {} / {}", permissionsMap.get(userPermission), request.getPath());
                return true;
            }
        }

        return false;
    }
}
