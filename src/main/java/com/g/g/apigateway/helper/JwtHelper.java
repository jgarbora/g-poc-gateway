package com.g.g.apigateway.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtHelper {

    private final static Base64.Decoder decoder = Base64.getDecoder();

    public static String findSubject(String token) throws JsonProcessingException {
        //  Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); <- return nulls
       return getPayload(token).get("sub").toString();
    }

    public static Map<String,Object> getPayload(String token) throws JsonProcessingException {

        String[] chunks = token.replace("Bearer ", "").split("\\.");

        // String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(payload, Map.class);
    }

}
