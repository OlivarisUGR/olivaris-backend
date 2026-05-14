package com.olivaris.olivaris_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.repositories.ActivityRepository;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.EntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;
import com.olivaris.olivaris_app.security.EntityValidator;

@SpringBootTest
public class EntityServiceDeleteTest {

    @Autowired
    private EntityService entityService;

    @MockitoBean
    private EntityRepository entityRepository;

    @MockitoBean
    private ActivityRepository activityRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private EntityRoleRepository entityRoleRepository;

    @MockitoBean
    private UserEntityRoleRepository userEntityRoleRepository;

    @MockitoBean
    private EntityValidator entityValidator;

    private EnabledEntity entity;

    @BeforeEach
    public void setUp() {
        when(entityValidator.currentUserIsAdmin()).thenReturn(true);

        entity = new EnabledEntity();
        entity.setId(1L);
        entity.setName("los olivos");
        entity.setNif("A1234567A");

        when(entityRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(activityRepository.detachEntityFromActivities(1L)).thenReturn(2);
    }

    @Test
    public void deleteEntityClearsActivityReferencesBeforeRemovingEntity() {
        ResponseEntity<Void> response = entityService.deleteEntity(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(activityRepository).detachEntityFromActivities(1L);
        verify(entityRepository).delete(entity);
    }
}