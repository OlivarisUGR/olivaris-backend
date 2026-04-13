package com.olivaris.olivaris_app.security;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.repositories.ActivityRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Component("activityValidator")
@AllArgsConstructor
public class ActivityValidator {

    private static final int LIMIT_DATE = 1;
    private final UserEntityRoleRepository userEntRoleRep;
    private final ActivityRepository actRep;
    private final UserEntityRoleRepository userEntityRoleRep;

    // An activity with future date can't be completed
    // An activity with past date can't be planned
    public boolean checkDateAndStatus(CreateActivityRequest body) {
        if(body.getDate().isAfter(LocalDate.now().plusYears(LIMIT_DATE))) {
            throw new IllegalArgumentException("No puedes planificar actividades a más de un año vista");
        } else if(body.getDate().isBefore(LocalDate.now()) && body.getStatus() != null &&
                    body.getStatus().equals(ActivityStatus.PLANNED)) {
            throw new IllegalArgumentException("No puedes planificar actividades para días pasados");
        } else if(body.getDate().isAfter(LocalDate.now()) && body.getStatus() != null &&
                    body.getStatus().equals(ActivityStatus.COMPLETED)) {
            throw new IllegalArgumentException("No puedes completar actividades para días futuros");
        }

        return true;
    }

    public boolean correctSeasonDate(int season) {
        int nowYear = Year.now().getValue();

        if(season < nowYear) {
            throw new IllegalArgumentException("No se puede crear una actividad para campañas de años anteriores");
        }

        return true;
    }

    public boolean checkAssigmentToUser(Long userIdToAssign, Long entityId) {
        // Get the current user logued on system
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = customUser.getId();

        boolean isSelf = currentUserId.equals(userIdToAssign);
        boolean belongToAnyEntity = userEntRoleRep.userBelongToAnyEntity(currentUserId);

        // User can creates an activity for himself if he doesn't belong to an entity or
        // he belong to all entities with admin role
        if(isSelf) {
            if(!belongToAnyEntity || (belongToAnyEntity && 
                userBelongsToAllEntAsAdmin(currentUserId))) {
                return true;
            }
        }

        // The activity will be created for a different user than the current if the user that
        // creates the activity have admin role on the entity and the other user has given permissions
        if(entityId == null) {
            return false;
        }

        boolean hasGivenPerm = userEntRoleRep.givesAllPermToEntity(userIdToAssign, entityId);
        boolean currentIsAdminOnEnt = userEntRoleRep.userRoleOnEnt(
            EntityRoleTypes.ROLE_ADMIN.toString(), currentUserId, entityId);

        if(currentIsAdminOnEnt) {
            return userEntRoleRep.usersBelongToSameEntity(userIdToAssign, currentUserId) && 
                hasGivenPerm;
        }

        return false;
    }

    public boolean canCreateActivities(Long userId, Long entityId, CreateActivityRequest body) {
        return this.checkAssigmentToUser(userId, entityId) && this.checkDateAndStatus(body) &&
            correctSeasonDate(Integer.parseInt(body.getSeason()));
    }

    public boolean canUpdateDeleteAct(Long activityId) {
        // Get the current user logued on system
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = customUser.getId();

        Long ownerId = actRep.getUserIdByActId(activityId)
            .orElseThrow(() -> new EntityNotFoundException(
                "No se ha encontrado la actividad en el sistema"
            ));

        boolean isSelf = currentUserId.equals(ownerId);

        // Can update / delete an activity:
        // - Himself: user that doesn't belong to an entity or he belong to all entities with admin role
        // - Other: user belongs to same entity with admin role and the other user gives all permission
        boolean belongToAnyEntity = userEntRoleRep.userBelongToAnyEntity(currentUserId);
    
        if(isSelf) {
            if(!belongToAnyEntity || (belongToAnyEntity && 
                userBelongsToAllEntAsAdmin(currentUserId))) {
                return true;
            }
        }

        // For other user:
        // Get the activity entity id 
        Optional<Long> optionalEntity = actRep.getEntityIdByActId(activityId);

        if(optionalEntity.isEmpty()) {
            return false;
        }
        
        Long entityId = optionalEntity.get();
        boolean hasGivenPerm = userEntRoleRep.givesAllPermToEntity(ownerId, entityId);
        boolean currentIsAdminOnEnt = userEntRoleRep.userRoleOnEnt(
            EntityRoleTypes.ROLE_ADMIN.toString(), currentUserId, entityId);

        if(currentIsAdminOnEnt) {
            return userEntRoleRep.usersBelongToSameEntity(ownerId, currentUserId) && 
                hasGivenPerm;
        }

        return false;
    }
 
    // Current user can get enclosure acts:
    // - Himself 
    // - Other user if he belongs to same entity than the current and current has admin role
    public boolean canGetEnclosuresActivities(Long userId, Long enclosureId, Long entityId) {
        // Get the current user logued on system
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = customUser.getId();
        boolean isSelf = currentUserId.equals(userId);
        
        // Current user will get enclosures activities for other user
        if(!isSelf) {
            if(entityId == null) {
                throw new IllegalArgumentException(
                    "El ID de la entidad a la que pertenece el usuario es necesario"
                );
            }

            boolean belongToSameEntity = userEntRoleRep.userBelongToEntity(currentUserId, entityId);
            
            if(!belongToSameEntity) {
                return false;
            }

            boolean hasEntityAdminRole = userEntRoleRep.userRoleOnEnt(
                EntityRoleTypes.ROLE_ADMIN.toString(), currentUserId, entityId);

            if(belongToSameEntity && !hasEntityAdminRole) {
                return false;
            }
        }

        return true;
    }

    private boolean userBelongsToAllEntAsAdmin(Long currentUserId) {
        List<UserEntityRole> entitiesList = userEntityRoleRep.findEntityByUserId(currentUserId);

        return !entitiesList.stream()
            .anyMatch(ent -> ent.getEntityRole().getName().equals(EntityRoleTypes.ROLE_FARMER.toString()));
    }
}
