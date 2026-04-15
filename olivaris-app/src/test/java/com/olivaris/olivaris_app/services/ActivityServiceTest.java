package com.olivaris.olivaris_app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.olivaris.olivaris_app.dto.ActivityCreatedResponse;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.fixtures.EntityFixtures;
import com.olivaris.olivaris_app.fixtures.UserFixtures;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.PhytoAct;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.ActivityType;
import com.olivaris.olivaris_app.repositories.ActivityRepository;
import com.olivaris.olivaris_app.repositories.EnclosureRepository;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;
import com.olivaris.olivaris_app.security.ActivityValidator;

@SpringBootTest
public class ActivityServiceTest {

    @Autowired
    private ActivityService actService;

    @MockitoBean
    private ActivityRepository actRep;

    @MockitoBean
    private UserRepository userRep;

    @MockitoBean
    private EnclosureRepository enclosureRep;

    @MockitoBean
    private EntityRepository entRep;

    @MockitoBean
    private PhytoActService phytoActServ;

    @MockitoBean
    private ActivityValidator actValidator;
    
    private User userBasic;
    private EnabledEntity entity;
    private Enclosure enclosure;
    private CreateActivityRequest validRequest;

    @BeforeEach
    public void setUp() {
        userBasic = UserFixtures.createBasicUser(new Role("ROLE_BASIC"));
        userBasic.setId(1L);

        entity = EntityFixtures.createBasicEntity();
        entity.setId(1L);

        enclosure = new Enclosure();
        enclosure.setId(1L);

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

    @Test
    public void createActivityIfNotExists() throws Exception {
        // Mock the validator PreAuthorize to enter on the method
        when(actValidator.canCreateActivities(userBasic.getId(), entity.getId(), validRequest))
            .thenReturn(true);

        // Mock all the repositories and services that are needed on create() activity method
        when(actRep.findByDateAndEnclosureIdAndUserIdAndTypeAndEntityId(
            validRequest.getDate(), enclosure.getId(), userBasic.getId(),
            ActivityType.PHYTOSANITARY, entity.getId()
        ))
        .thenReturn(Optional.empty());

        when(userRep.findById(userBasic.getId()))
            .thenReturn(Optional.of(userBasic));

        when(enclosureRep.findById(1L))
            .thenReturn(Optional.of(enclosure));

        when(entRep.findById(entity.getId()))
            .thenReturn(Optional.of(entity));

        // When createPhytoActivity() is called, the function inside of .thenAnswer will be executed
        // and it will returned the created object.
        // It is necessary to mock like this because the real service will executes the createPhytoAct
        // for each phyto act, and this is done by other services, so is necessary to mock the function too
        when(phytoActServ.createPhytoActivity(any(Activity.class), any(CreatePhytoActReq.class)))
            .thenAnswer(invocation -> {
                Activity act = invocation.getArgument(0);
                CreatePhytoActReq phytoActReq = invocation.getArgument(1);

                // Create new Phyto Activity object
                return PhytoAct.builder()
                    .activity(act)
                    .applicationDate(phytoActReq.getApplicationDate())
                    .reason(phytoActReq.getReason())
                    .dose(phytoActReq.getDose())
                    .doseUnit(phytoActReq.getDoseUnit())
                    .totalAmount(phytoActReq.getTotalAmount())
                    .applicationMethod(phytoActReq.getApplicationMethod())
                    .applicationMachinery(phytoActReq.getApplicationMachinery())
                    .applicatorName(phytoActReq.getApplicatorName())
                    .applicatorNif(phytoActReq.getApplicatorNif())
                    .crops(phytoActReq.getCrops())
                    .area(phytoActReq.getArea())
                    .build();
            });

        // When save() is executed, it will return the same activity object that was passed
        // to the method
        when(actRep.save(any(Activity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<ActivityCreatedResponse> response = assertDoesNotThrow(() ->
            actService.create(userBasic.getId(), enclosure.getId(), entity.getId(), validRequest)
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

}
