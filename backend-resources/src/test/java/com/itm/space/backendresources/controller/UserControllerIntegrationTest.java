package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BackendResourcesApplication;
import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendResourcesApplication.class)
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private UserService userService;

    @Test
    void helloWithModeratorRoleReturnsUsername() throws Exception {
        mvc.perform(get("/api/users/hello")
                        .with(jwt()
                                .jwt(jwt -> jwt.subject("moderator"))
                                .authorities(() -> "ROLE_MODERATOR")))
                .andExpect(status().isOk())
                .andExpect(content().string("moderator"));
    }

    @Test
    void createUserInvalidRequestReturns400() throws Exception {
        UserRequest request = new UserRequest(
                "badguy",
                "badEmail",
                "1",
                "Baddy",
                "GummyGuy"
        );
        mvc.perform(requestWithContent(post("/api/users"), request)
                        .with(jwt().authorities(() -> "ROLE_MODERATOR")))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any());
    }

    @Test
    void getUserById_withModeratorRole_returnsUser() throws Exception {

        UUID id = UUID.randomUUID();
        UserResponse response = new UserResponse(
                "Test",
                "TestTest",
                "test@mail.com",
                List.of("ROLE_USER"),
                List.of("group1")
        );
        when(userService.getUserById(id)).thenReturn(response);
        mvc.perform(get("/api/users/{id}", id)
                        .with(jwt().authorities(() -> "ROLE_MODERATOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("TestTest"))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
        verify(userService).getUserById(id);
    }
}