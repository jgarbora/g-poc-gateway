package com.g.g.apigateway.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class RedisAdapter {

    private RedisServer redisServer;

    @PostConstruct
    public void postConstruct() {
        redisServer = RedisServer.builder().port(6379).build();
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        try {
            redisServer.stop();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


}
