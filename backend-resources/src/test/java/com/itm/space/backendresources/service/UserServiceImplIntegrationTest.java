package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceImplIntegrationTest {

    @Mock
    private Keycloak keycloakClient;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private final String realm = "ITM";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "realm", realm);
    }

    @Test
    void createUserCallKeycloak() {
        UserRequest request = new UserRequest(
                "TestGuy1",
                "testguy1@example.com",
                "test12345",
                "Test1",
                "TestTest1");
        RealmResource realmResource = mock(RealmResource.class);
        UsersResource usersResource = mock(UsersResource.class);
        Response response = mock(Response.class);
        when(keycloakClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        userService.createUser(request);
        verify(usersResource, times(1)).create(any(UserRepresentation.class));
    }

    @Test
    void getUserByIdReturnUserResponse() {

        UUID userId = UUID.randomUUID();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("TestGuy2");
        userRepresentation.setEmail("testguy2@example.com");
        userRepresentation.setFirstName("Test2");
        userRepresentation.setLastName("TestTest2");
        List<RoleRepresentation> roles = Collections.emptyList();
        List<GroupRepresentation> groups = Collections.emptyList();
        RealmResource realmResource = mock(RealmResource.class);
        UsersResource usersResource = mock(UsersResource.class);
        UserResource userResource = mock(UserResource.class);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        MappingsRepresentation mappingsRepresentation = mock(MappingsRepresentation.class);
        when(keycloakClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId.toString())).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.getAll()).thenReturn(mappingsRepresentation);
        when(mappingsRepresentation.getRealmMappings()).thenReturn(roles);
        when(userResource.groups()).thenReturn(groups);
        UserResponse expectedResponse = new UserResponse(
                "Test2",
                "TestTest2",
                "testguy2@example.com",
                Collections.emptyList(),
                Collections.emptyList()
        );
        when(userMapper.userRepresentationToUserResponse(userRepresentation, roles, groups))
                .thenReturn(expectedResponse);
        UserResponse actualResponse = userService.getUserById(userId);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userMapper, times(1)).userRepresentationToUserResponse(userRepresentation, roles, groups);
    }
}
