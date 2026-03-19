package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.PhytoAct;
import com.olivaris.olivaris_app.models.User;
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
    public ResponseEntity<ActivityDto> create(CreateActivityRequest body) {
        // If the activity exists -> Get and update
        // else -> create a new activity
        Activity newAct = actRep.findByDateAndEnclosureIdAndUserIdAndType(
                body.getDate(), body.getEnclosureId(), body.getUserId(), body.getType())
            .map(actExists ->  {
                if(body.getDescription() != null && !body.getDescription().isBlank()) {
                    actExists.setDescription(body.getDescription());
                }

                return actExists;
            })
            .orElseGet(() -> {
                // Get the user and enclosure
                User userDb = userRep.findById(body.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(body.getUserId().toString()));
                    
                Enclosure enclosureDb = enclosureRep.findById(body.getEnclosureId())
                    .orElseThrow(() -> new EntityNotFoundException("El recinto no existe en la base de datos"));

                // Create the activity and save it on database
                return Activity.builder()
                    .user(userDb)
                    .enclosure(enclosureDb)
                    .type(body.getType())
                    .date(body.getDate())
                    .season(body.getSeason())
                    .description(body.getDescription() != null ? body.getDescription() : null)
                    .phytoAct(new ArrayList<PhytoAct>())
                    .build();
            });

        // Create the phytosanitary activities
        body.getPhytoAct().stream()
            .forEach(phytoActDto -> {
                PhytoAct phytoAct = phytoActService.createPhytoActivity(newAct, phytoActDto);
                newAct.getPhytoAct().add(phytoAct);
            });
        
        Activity actDb = actRep.save(newAct);
        
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
}
