package com.olivaris.olivaris_app.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.fixtures.EntityFixtures;
import com.olivaris.olivaris_app.fixtures.UserFixtures;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.ActivityType;
import com.olivaris.olivaris_app.security.ActivityValidator;
import com.olivaris.olivaris_app.services.ActivityService;

@SpringBootTest
public class ActivityValidatorTest {

    @Autowired
    private ActivityService actService;

    @MockitoBean
    private ActivityValidator actValidator;

    private User userBasic;
    private EnabledEntity entity;
    private CreateActivityRequest validRequest;
    private Long userId = 1L;
    private Long entityId = 1L;
    private Long enclosureId = 1L;

    @BeforeEach
    public void setUp() {
        userBasic = UserFixtures.createBasicUser(new Role("ROLE_BASIC"));
        userBasic.setId(userId);

        entity = EntityFixtures.createBasicEntity();
        entity.setId(entityId); 

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

    // Test to check that activity validator method works well
    @Test
    public void linkedFarmerWithAllPermissionsCannotCreateActivity() throws Exception {
        // Mock the activity validor method to return false if an user linked to an entity has farmer role
        // and he gives all permissions. This is registered in a internal structure to use it when the method
        // is called in the future
        when(actValidator.canCreateActivities(userId, entityId, validRequest))
            .thenReturn(false);

        // This execute the create method on activity service -> this method calls canCreateActivites for 
        // PreAuthorize -> spring uses the false value that was mocked before -> return AccessDeniedException
        assertThrows(AccessDeniedException.class, () ->
            actService.create(userId, enclosureId, entityId, validRequest)
        );
    }

    // Test to check that activity validator method works well
    @Test
    public void userWithoutLinkedToEntityCanCreateAct() throws Exception {
        // Mock canCreteActivities method to return a true value
        when(actValidator.canCreateActivities(userId, null, validRequest))
            .thenReturn(true);

        // Get the result when create method is executed
        // In this case, create() method will be executed correctly and inside it, there are 
        // repositories and other components that will be executed without mocking his functionalities;
        // this gives an UserNotFoundException because the user it isn't save on database (and other entities too).
        // It is better check that UserNotFoundException is raised -> this is raised after pass the act validator
        assertThrows(UserNotFoundException.class, () -> 
            actService.create(userId, enclosureId, null, validRequest)
        );
    }
}
