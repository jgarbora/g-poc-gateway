package com.g.g.apigateway.adapter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class Auth0Adapter {

    @Value("${baseurl:https://jgarbora.eu.auth0.com}")
    private String baseUrl;

    @Value("${auth0.management.api.clientId}")
    private String clientId;

    @Value("${auth0.management.api.clientSecret}")
    private String clientSecret;

    private ResponseEntity<LinkedHashMap> tokenResponse;

    private RestTemplate restTemplate = new RestTemplate();

    public void init() {
        Map<String, String> body = new HashMap<>();
        body.put("client_id",clientId);
        body.put("client_secret",clientSecret);
        body.put("audience","https://jgarbora.eu.auth0.com/api/v2/");
        body.put("grant_type","client_credentials");

        log.debug("clientId: {} , clientSecret: {}", clientId, clientSecret);

        tokenResponse = restTemplate.exchange(baseUrl+"/oauth/token", HttpMethod.POST, new HttpEntity<>(body, buildHeaders()), LinkedHashMap.class);
    }

    // TODO NEED TO REFRESH THE TOKEN

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }

    @Cacheable("subject")
    public LinkedHashMap findUser(String subject) {
        // example https://jgarbora.eu.auth0.com/api/v2/users/auth0%7C60f879147ddc3f0069ecd7bf
        String url = String.format("%s/api/v2/users/%s", baseUrl, subject);
        log.debug("{}",url);

        ResponseEntity<LinkedHashMap> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(buildHeadersForAuth0Api()), LinkedHashMap.class);
        if (responseEntity.hasBody() && responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        log.error("get user fail, {}",responseEntity.getStatusCode());
        throw new RuntimeException("couldn't get user information for subject " + subject);
    }



    private HttpHeaders buildHeadersForAuth0Api() {
        if (tokenResponse == null) {
            init();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", tokenResponse.getBody().get("access_token").toString()));
        return headers;
    }
}
