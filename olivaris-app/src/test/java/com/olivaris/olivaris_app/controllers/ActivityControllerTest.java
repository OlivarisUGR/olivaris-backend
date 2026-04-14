package com.olivaris.olivaris_app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tools.jackson.databind.ObjectMapper;
import com.olivaris.olivaris_app.dto.ActivityCreatedResponse;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.fixtures.EntityFixtures;
import com.olivaris.olivaris_app.fixtures.UserFixtures;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.ActivityType;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.repositories.UserRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;
import com.olivaris.olivaris_app.security.SecurityConfig;
import com.olivaris.olivaris_app.security.ActivityValidator;
import com.olivaris.olivaris_app.services.ActivityService;
import com.olivaris.olivaris_app.services.JwtService;

// Controller tests is a type of test that check if the endpoints are connected correctly, he recieves the correct
// parameters and return the correct response
@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class ActivityControllerTest {

    @MockitoBean
    private ActivityService actService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private JwtService jwtService;
    
    @MockitoBean
    private UserRepository userRep;
    
    @MockitoBean
    private UserEntityRoleRepository userEntityRoleRep;
    
    @MockitoBean
    private ActivityValidator activityValidator;
    
    private CreateActivityRequest validRequest;
    
    
    @BeforeEach
    public void setUp() { 
        CreatePhytoActReq phytoAct = new CreatePhytoActReq(
            5L,
            LocalDate.of(2026, 4, 10),
            "Prevencion",
            2.0,
            "l/ha",
            8.0,
            "Pulverizacion",
            "Atomizador",
            "Pepe",
            "12345678A",
            "Olivo",
            1.5
        );
    
        validRequest = new CreateActivityRequest(
            LocalDate.now(),
            "2026",
            "Actividad fitosanitaria",
            ActivityType.PHYTOSANITARY,
            ActivityStatus.PLANNED,
            List.of(phytoAct)
        );
    }

    // Test to check that a basic user whose is not linked to an entity can creates an activity
    @Test
    public void createActReturnsCreatedWhenRequestIsValid() throws Exception {
        ActivityCreatedResponse mockResponse = ActivityCreatedResponse.builder()
            .activityId(99L)
            .phytoActId(List.of(201L))
            .createdAt(LocalDateTime.now())
            .message("Actividad creada")
            .build();
    
        when(actService.create(
            eq(1L), 
            eq(10L), 
            eq(3L),
            any(CreateActivityRequest.class)
        ))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(mockResponse));
    
        String json = objectMapper.writeValueAsString(validRequest);
    
        mockMvc.perform(MockMvcRequestBuilders.post("/api/activity/user/1/enclosure/10")
            .with(user(authenticatedUser(1L, "ROLE_BASIC")))
                .param("entityId", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.activityId").value(99L))
            .andExpect(jsonPath("$.message").value("Actividad creada"));
    
        verify(actService).create(eq(1L), eq(10L), eq(3L), any(CreateActivityRequest.class));
    }

    @Test
    public void linkedFarmerWithAllPermissionsCannotCreateActivity() throws Exception {
        // Create a basic user linked to an entity with all permissions
        UserEntityRole userEntityRole = createFarmerAssignmentWithAllPermissions(1L, 1L);
        User linkedUser = userEntityRole.getUser();
        Long linkedUserId = linkedUser.getId();
        Long linkedEntityId = userEntityRole.getEnabledEntity().getId();
        Long enclosureId = 1L;

        when(actService.create(
            eq(linkedUserId),
            eq(enclosureId),
            eq(linkedEntityId),
            any(CreateActivityRequest.class)
        ))
        .thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).build());

        String json = objectMapper.writeValueAsString(validRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(
        "/api/activity/user/{userId}/enclosure/{enclosureId}", 
                    linkedUserId, 
                    enclosureId
                )
                .with(user(new CustomUserDetails(linkedUser)))
                .param("entityId", linkedEntityId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isForbidden());

        // With this verify, it checks that the 403 error comes from services mock and not from 
        // a security filter
        verify(actService).create(
            eq(linkedUserId), 
            eq(enclosureId), 
            eq(linkedEntityId), 
            any(CreateActivityRequest.class)
        );
    }

    private CustomUserDetails authenticatedUser(Long id, String roleName) {
        Role role = new Role(roleName);
        User user = UserFixtures.createBasicUser(role);
        user.setId(id);

        return new CustomUserDetails(user);
    }

    private UserEntityRole createFarmerAssignmentWithAllPermissions(Long userId, Long entityId) {
        User user = UserFixtures.createBasicUser(new Role("ROLE_BASIC"));
        user.setId(userId);

        EnabledEntity entity = EntityFixtures.createBasicEntity();
        entity.setId(entityId);

        EntityRole farmerRole = new EntityRole(EntityRoleTypes.ROLE_FARMER.toString());

        return new UserEntityRole(
            user,
            entity,
            farmerRole,
            true,
            true,
            true,
            true
        );
    }

}
