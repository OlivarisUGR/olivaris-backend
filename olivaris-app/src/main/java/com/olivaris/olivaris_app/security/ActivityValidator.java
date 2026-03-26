package com.olivaris.olivaris_app.security;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.ActivityRepository;
import com.olivaris.olivaris_app.repositories.EntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Component("activityValidator")
@AllArgsConstructor
public class ActivityValidator {

    private static final int LIMIT_DATE = 1;
    private final UserEntityRoleRepository userEntRoleRep;
    private final ActivityRepository actRep;
    private final EntityRoleRepository entRoleRep;

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

        // Can create an activity:
        // Admin -> can creates an activity for himself or for any user whose is registered on system
        if(hasRole(customUser, RoleTypes.ROLE_ADMIN)) {
            return true;
        }

        boolean isSelf = currentUserId.equals(userIdToAssign);
        boolean belongToAnyEntity = userEntRoleRep.userBelongToAnyEntity(currentUserId);

        // User can creates an activity for himself if he doesn't belong to an entity or
        // he belong to the entity but he hasn't given permission
        if(isSelf) {
            if(!belongToAnyEntity && entityId == null) {
                return true;
            }

            // If the user belong to an entity, it is necessary the entity Id to search the permissions given
            if(entityId == null) {
                return false;
            }
            
            boolean userBelongToEnt = userEntRoleRep.userBelongToEntity(currentUserId, entityId);
            boolean hasGivenPerm = userEntRoleRep.givesAllPermToEntity(currentUserId, entityId);

            return userBelongToEnt && !hasGivenPerm;
        }

        // The activity will be created for a different user than the current if the user that
        // creates the activity have admin role on the entity and the other user has given permissions
        if(entityId == null) {
            return false;
        }

        boolean hasGivenPerm = userEntRoleRep.givesAllPermToEntity(userIdToAssign, entityId);
        Long roleAdminId = entRoleRep.getRoleIdByName(EntityRoleTypes.ROLE_ADMIN.toString())
            .orElseThrow(() -> new EntityNotFoundException("El rol de la entidad no existe"));

        boolean currentIsAdminOnEnt = userEntRoleRep.userRoleOnEnt(
            roleAdminId, currentUserId, entityId);

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

        // Can update an activity:
        // Admin -> can updates an activity for himself or for any user whose is registered on system
        if(hasRole(customUser, RoleTypes.ROLE_ADMIN)) {
            return true;
        }

        Long ownerId = actRep.getUserIdByActId(activityId)
            .orElseThrow(() -> new EntityNotFoundException(
                "No se ha encontrado la actividad en el sistema"
            ));
        
        // Get the activity entity id 
        Optional<Long> optEntityId = actRep.getEntityIdByActId(activityId);

        // If the owner is the same than the current user and he doesn't belong to an entity or he belongs
        // to an entity but he doesn't give permission -> can update / delete activity
        if(currentUserId.equals(ownerId)) {
            return optEntityId
                .map(entId -> !userEntRoleRep.givesAllPermToEntity(currentUserId, entId))
                .orElse(true); 
        }

        // If the owner is different to the current user, owner gives permissions to entity and the current 
        // user belong to the same entity with admin role -> can update / delete activity
        return optEntityId
            .map(entId -> {
                Long roleAdminId = entRoleRep.getRoleIdByName(EntityRoleTypes.ROLE_ADMIN.toString())
                    .orElseThrow(() -> new EntityNotFoundException("El rol de la entidad no existe"));

                boolean currentIsAdminInThisEnt = userEntRoleRep.userRoleOnEnt(
                    roleAdminId, currentUserId, entId);
                
                boolean ownerGavePermsInThisEnt = userEntRoleRep.givesAllPermToEntity(ownerId, entId);

                return currentIsAdminInThisEnt && ownerGavePermsInThisEnt;
            })
            .orElse(false);
    }

    private boolean hasRole(CustomUserDetails user, RoleTypes role) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }
}
