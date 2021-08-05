package com.g.g.apigateway;

import com.auth0.dto.api.v2.users.*;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootTest
@Slf4j
class CrudUserIntegrationTest {

    String baseurl = "http://localhost:8080";

    RestTemplate restTemplate = new RestTemplate();

    Faker faker = new Faker();

    private static final Map<String, String> roleMap = Map.of("admin", "rol_vJ8LvtkaBizBowVd", "user", "rol_n5ecegE12SByX7Oo");

    @Test
    void crudAdminTest() throws Exception {

        // logging as an existing admin

        // create an admin user
        ResponseEntity<CreateUserResponse> createUserResponse = restTemplate.exchange(baseurl + "/api/v0/users", HttpMethod.POST, new HttpEntity<>(createUserRequestBuilder(), buildHeaders()), CreateUserResponse.class);
        Assertions.assertEquals(201, createUserResponse.getStatusCodeValue()); // 201 created

        // assign admin role
        ResponseEntity<AssignRolesToAUserResponse> assignRolesToAUserResponse = restTemplate.exchange(baseurl + "/api/v0/users/" + createUserResponse.getBody().userId + "/roles", HttpMethod.POST, new HttpEntity<>(assignRolesToAUserRequestBuilder("admin"), buildHeaders()), AssignRolesToAUserResponse.class);
        Assertions.assertEquals(204, assignRolesToAUserResponse.getStatusCodeValue()); // 204	Roles successfully associated with user.

        // delete admin
        ResponseEntity<DeleteUserResponse> deleteUserResponse = restTemplate.exchange(baseurl + "/api/v0/users/" + createUserResponse.getBody().userId, HttpMethod.DELETE, new HttpEntity<>(buildHeaders()), DeleteUserResponse.class);
        Assertions.assertEquals(204, deleteUserResponse.getStatusCodeValue()); // 204	User successfully deleted.

    }

    @Test
    void crudUserTest() throws Exception {

        // logging as an existing admin

        // create an regular user
        ResponseEntity<CreateUserResponse> createUserResponse = restTemplate.exchange(baseurl + "/api/v0/users", HttpMethod.POST, new HttpEntity<>(createUserRequestBuilder(), buildHeaders()), CreateUserResponse.class);
        Assertions.assertEquals(201, createUserResponse.getStatusCodeValue()); // 201 created

        // assign user role
        ResponseEntity<AssignRolesToAUserResponse> assignRolesToAUserResponse = restTemplate.exchange(baseurl + "/api/v0/users/" + createUserResponse.getBody().userId + "/roles", HttpMethod.POST, new HttpEntity<>(assignRolesToAUserRequestBuilder("user"), buildHeaders()), AssignRolesToAUserResponse.class);
        Assertions.assertEquals(204, assignRolesToAUserResponse.getStatusCodeValue()); // 204	Roles successfully associated with user.

        // delete user
        if (false) {
            ResponseEntity<DeleteUserResponse> deleteUserResponse = restTemplate.exchange(baseurl + "/api/v0/users/" + createUserResponse.getBody().userId, HttpMethod.DELETE, new HttpEntity<>(buildHeaders()), DeleteUserResponse.class);
            Assertions.assertEquals(204, deleteUserResponse.getStatusCodeValue()); // 204	User successfully deleted.
        }

    }

    private AssignRolesToAUserRequest assignRolesToAUserRequestBuilder(String role) {
        return AssignRolesToAUserRequest.builder()
                .roles(Arrays.asList(roleMap.get(role)))
                .build();
    }

    private CreateUserRequest createUserRequestBuilder() {

        String emailAddress = faker.internet().emailAddress();
        String password = faker.internet().password();
        log.info("emailAddress {}, password: {} ", emailAddress, password);

        return CreateUserRequest.builder()
                .email(emailAddress)
                .givenName(faker.name().name())
                .picture("https://i.pravatar.cc/200")
                .connection("Username-Password-Authentication")
                .password(password)
                .build();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1rVXpOMFV5TVRrMU9URkJPVGRDT1VJMk9ERTNORGxETkVNelF6TXpOVGMzTURrM01qZENNZyJ9.eyJpc3MiOiJodHRwczovL2pnYXJib3JhLmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw2MGZlZDA3NTQ1MGIwNzAwNmE3YjM5OGQiLCJhdWQiOlsiZ2VuZXZhLXBvYy1hcGkiLCJodHRwczovL2pnYXJib3JhLmV1LmF1dGgwLmNvbS91c2VyaW5mbyJdLCJpYXQiOjE2MjgxMDM2MTcsImV4cCI6MTYyODE5MDAxNywiYXpwIjoibGpuS05CbEJiYmwyS0FaN3dqUElKcU1NTmw4WFJZaEwiLCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwicGVybWlzc2lvbnMiOlsiY3JlYXRlOnJvbGVfbWVtYmVycyIsImNyZWF0ZTp1c2VyIiwicmVhZDp1c2VyIiwidXBkYXRlOnVzZXIiXX0.L_H73zM0xlarZhZYhQqLgSLqjW-ywmyt6SG_awu86bDlDKHgdp_CJaqTt79NAkoptwo9Poyp1dB0-7jhKgesrZSZ_Hf5Vk-styN-vscC6ROajUBw9GsZSZnwbIrRxC1NNxO1LChgLrE5d_OLNbOq2csF54pbFELU-KMdLUTfxurNOhmeE1OR327s0FKUXXFNeWgD1LW8dpIAT1kNShMLrnQ40f91UToxWE31RI7nsDk_1CNoloY9VhtDgmFy-RxisqUPFr58P1IoIVzf9wO8bxSG7abkfVpuocCUYm-Ctav4Ncm8VMhqRShshUCaOjIycyWgXARsyZOSIguLPY4EDQ"));
        return headers;
    }

}
