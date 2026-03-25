package com.olivaris.olivaris_app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.ActivityDto;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.PhytoActivityDto;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.PhytoAct;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.repositories.ActivityRepository;
import com.olivaris.olivaris_app.repositories.EnclosureRepository;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import com.olivaris.olivaris_app.dto.ActivityCreatedResponse;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository actRep;
    private final UserRepository userRep;
    private final EnclosureRepository enclosureRep;
    private final PhytoActService phytoActService;
    private final EntityRepository entityRep;
    
    @Transactional
    @Override
    @PreAuthorize("@activityValidator.canCreateActivities(#userId, #entityId, #body)")
    public ResponseEntity<ActivityCreatedResponse> create(
        Long userId,
        Long enclosureId,
        Long entityId,
        CreateActivityRequest body
    ) {
        // If activity exists -> Get and update it
        // else -> create a new activity
        Activity act = actRep.findByDateAndEnclosureIdAndUserIdAndTypeAndEntityId(
                body.getDate(), enclosureId, userId, body.getType(), entityId)
            .orElseGet(() -> createNewActivity(userId, enclosureId, entityId, body));

        if(body.getDescription() != null && !body.getDescription().isBlank()) {
            act.setDescription(body.getDescription());
        }

        if(body.getStatus() != null) {
            act.setStatus(body.getStatus());
        } else if(act.getStatus() == null) {
            act.setStatus(body.getDate().isAfter(LocalDate.now()) ? 
                ActivityStatus.PLANNED : ActivityStatus.COMPLETED);
        }

        // Create the phytosanitary activities
        body.getPhytoAct().stream()
            .forEach(phytoActDto -> {
                PhytoAct phytoAct = phytoActService.createPhytoActivity(act, phytoActDto);
                act.getPhytoAct().add(phytoAct);
            });
        
        Activity actDb = actRep.save(act);
        
        // Create the DTO and return the response
        ActivityCreatedResponse actDto = new ActivityCreatedResponse(
            actDb.getId(),
            actDb.getPhytoAct().stream()
                .map(pA -> pA.getId())
                .toList(),
            LocalDateTime.now(),
            "Una actividad de tipo fitosanitaria ha sido registrada"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(actDto);
    }

    @Transactional
    @Override
    @PreAuthorize("@activityValidator.canUpdateDeleteAct(#id)")
    public ResponseEntity<Void> delete(Long id) {
        // Find the activity
        Activity actDb = actRep.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("La actividad con ese ID no existe"));

        actRep.delete(actDb);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    @PreAuthorize("@activityValidator.canUpdateDeleteAct(#activityId)")
    public ResponseEntity<PhytoActivityDto> update(
        Long activityId, 
        Long phytoActId,
        UpdatePhytoActReq body
    ) {
        // Check if activity exists
        Activity actDb = actRep.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException("La actividad con ID " + activityId + " no existe"));

        if(body.getDescription() != null) {
            actDb.setDescription(body.getDescription());
        }

        // If the activity is completed, his status can't be updated
        if(body.getStatus() != null && !actDb.getStatus().equals(ActivityStatus.COMPLETED)) {
            actDb.setStatus(body.getStatus());
        } else {
            throw new IllegalArgumentException("No se puede modificar el estado de la actividad");
        }

        PhytoAct phytoActDb = phytoActService.updatePhytoAct(phytoActId, body);

        PhytoActivityDto dto = new PhytoActivityDto(
            activityId,
            phytoActId,
            "La actividad fitosanitaria ha sido actualizada con éxito"
        );

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    private Activity createNewActivity(
        Long userId,
        Long enclosureId,
        Long entityId,
        CreateActivityRequest body
    ) {
        // Get the user and enclosure
        User userDb = userRep.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));
            
        Enclosure enclosureDb = enclosureRep.findById(enclosureId)
            .orElseThrow(() -> new EntityNotFoundException("El recinto no existe en la base de datos"));

        // Get the enabled entity if the user belong to an entity and this activity will be registered
        // on an entity
        EnabledEntity entityDb = null;

        if(entityId != null) {
            entityDb = entityRep.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException("La entidad habilitada no existe"));
        }

        // Create the activity and save it on database
        Activity act = Activity.builder()
            .user(userDb)
            .enclosure(enclosureDb)
            .entity(entityDb)
            .type(body.getType())
            .date(body.getDate())
            .season(body.getSeason())
            .description(body.getDescription() != null ? body.getDescription() : null)
            .phytoAct(new ArrayList<PhytoAct>())
            .build();

        return act;
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<ActivityDto>> getEnclosuresAct(Long enclosureId, String season) {
        List<Activity> enclosuresAct = null;

        if(season == null) {
            enclosuresAct = actRep.findByEnclosureId(enclosureId);
        } else {
            enclosuresAct = actRep.findByEnclosureIdAndSeason(enclosureId, season);
        }

        List<ActivityDto> enclosuresActDto = enclosuresAct.stream()
            .map(ActivityDto::fromEntity)
            .toList();
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(enclosuresActDto);
    }
}
