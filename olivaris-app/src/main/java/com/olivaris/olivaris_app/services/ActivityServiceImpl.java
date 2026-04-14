package com.olivaris.olivaris_app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.ActivityDto;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.dto.UpdateActRequest;
import com.olivaris.olivaris_app.exceptions.ActivityException;
import com.olivaris.olivaris_app.exceptions.EntityExistsException;
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
import com.olivaris.olivaris_app.repositories.UserPlotRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import com.olivaris.olivaris_app.dto.ActivityCreatedResponse;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository actRep;
    private final UserRepository userRep;
    private final UserPlotRepository userPlotRep;
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
        Optional<Activity> optionalAct = actRep.findByDateAndEnclosureIdAndUserIdAndTypeAndEntityId(
                body.getDate(), enclosureId, userId, body.getType(), entityId);

        final Activity act;

        if(optionalAct.isEmpty()) {
            if(!sameYear(body.getSeason(), body.getDate())) {
                throw new ActivityException(
                    "La camapaña tiene que coincidir con el año de la actividad"
                );
            }

            act = createNewActivity(userId, enclosureId, entityId, body);
        } else {
            throw new EntityExistsException("La actividad ya existe en el sistema");
        }

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
    public ResponseEntity<ActivityDto> updateActivity(
        Long activityId, 
        UpdateActRequest body
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
        } 

        List<PhytoAct> phytoActListDb = phytoActService.updatePhytoActivities(body.getPhytoActs());

        ActivityDto actDto = ActivityDto.fromEntity(actDb, null);
    
        return ResponseEntity.status(HttpStatus.OK).body(actDto);
    }

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("@activityValidator.canGetEnclosuresActivities(#userId, #enclosureId, #entityId)")
    public ResponseEntity<List<ActivityDto>> getEnclosuresActByUser(
        Long userId, 
        Long enclosureId, 
        Long entityId,
        String season
    ) {
        List<Activity> enclosuresAct = null;

        if(season == null) {
            enclosuresAct = actRep.findByEnclosureIdAndUserId(enclosureId, userId);
        } else {
            enclosuresAct = actRep.findByEnclosureIdAndSeasonAndUserId(enclosureId, season, userId);
        }

        List<ActivityDto> enclosuresActDto = enclosuresAct.stream()
            .map(act -> {
                Long plotId = act.getEnclosure().getPlot().getId();
                Long activityUserId = act.getUser().getId();
                String plotName = userPlotRep
                    .getPlotNameByUserIdAndPlotId(activityUserId, plotId)
                    .orElse(null);
                return ActivityDto.fromEntity(act, plotName);
            })
            .toList();
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(enclosuresActDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<ActivityDto>> getUserActivities(Long userId) {
        List<Activity> activitiesDb = actRep.findByUserId(userId);
        
        List<ActivityDto> activitiesDto = activitiesDb.stream()
            .map(act -> {
                Long plotId = act.getEnclosure().getPlot().getId();
                Long activityUserId = act.getUser().getId();
                String plotName = userPlotRep
                    .getPlotNameByUserIdAndPlotId(activityUserId, plotId)
                    .orElse(null);
                return ActivityDto.fromEntity(act, plotName);
            })
            .toList();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(activitiesDto);
    }

    @Transactional
    @Override
    @PreAuthorize("@activityValidator.canUpdateDeleteAct(#activityId)")
    public ResponseEntity<ActivityCreatedResponse> addNewPhytoActivity(
        Long activityId, 
        CreatePhytoActReq phytoActInfo
    ) {
        // Fin the activity on database
        Activity actDb = actRep.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException(
                "La actividad no existe en el sistema"
            ));
        
        // Create the new Phyto Activity
        PhytoAct newPhytoAct = phytoActService.createPhytoActivity(actDb, phytoActInfo);
        actDb.getPhytoAct().add(newPhytoAct);
        actDb = actRep.save(actDb);
        
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

    private Activity createNewActivity(
        Long userId,
        Long enclosureId,
        Long entityId,
        CreateActivityRequest body
    ) {
        // Get the user and enclosure
        User userDb = userRep.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));
            
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

    private boolean sameYear(String season, LocalDate date) {
        if(season == null || date == null) {
            return false;
        }

        try {
            return Integer.parseInt(season) == date.getYear();
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
