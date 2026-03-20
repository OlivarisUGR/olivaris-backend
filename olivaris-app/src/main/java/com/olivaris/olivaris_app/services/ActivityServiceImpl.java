package com.olivaris.olivaris_app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.PhytoActivityDto;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.PhytoAct;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.repositories.ActivityRepository;
import com.olivaris.olivaris_app.repositories.EnclosureRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import com.olivaris.olivaris_app.dto.ActivityDto;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository actRep;
    private final UserRepository userRep;
    private final EnclosureRepository enclosureRep;
    private final PhytoActService phytoActService;
    
    @Transactional
    @Override
    @PreAuthorize("@activityValidator.checkDateAndStatus(#body)")
    public ResponseEntity<ActivityDto> create(CreateActivityRequest body) {
        // If activity exists -> Get and update it
        // else -> create a new activity
        Activity act = actRep.findByDateAndEnclosureIdAndUserIdAndType(
                body.getDate(), body.getEnclosureId(), body.getUserId(), body.getType())
            .orElseGet(() -> createNewActivity(body));

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
        ActivityDto actDto = new ActivityDto(
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
    public ResponseEntity<Void> delete(Long id) {
        // Find the activity
        Activity actDb = actRep.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("La actividad con ese ID no existe"));

        actRep.delete(actDb);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    public ResponseEntity<PhytoActivityDto> update(Long activityId, UpdatePhytoActReq body) {
        // Check if activity exists
        Activity actDb = actRep.findById(activityId)
            .orElseThrow(() -> new EntityNotFoundException("La actividad con ID " + activityId + " no existe"));

        actDb.setStatus(body.getStatus());

        PhytoAct phytoActDb = phytoActService.updatePhytoAct(body);

        PhytoActivityDto dto = new PhytoActivityDto(
            activityId,
            body.getPhytoActId(),
            "La actividad fitosanitaria ha sido actualizada con éxito"
        );

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    private Activity createNewActivity(CreateActivityRequest body) {
        // Get the user and enclosure
        User userDb = userRep.findById(body.getUserId())
            .orElseThrow(() -> new UserNotFoundException(body.getUserId().toString()));
            
        Enclosure enclosureDb = enclosureRep.findById(body.getEnclosureId())
            .orElseThrow(() -> new EntityNotFoundException("El recinto no existe en la base de datos"));

        // Create the activity and save it on database
        Activity act = Activity.builder()
            .user(userDb)
            .enclosure(enclosureDb)
            .type(body.getType())
            .date(body.getDate())
            .season(body.getSeason())
            .description(body.getDescription() != null ? body.getDescription() : null)
            .phytoAct(new ArrayList<PhytoAct>())
            .build();

        return act;
    }
}
