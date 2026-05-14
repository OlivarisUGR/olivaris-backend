package com.olivaris.olivaris_app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Component("entityValidator")
@AllArgsConstructor
public class EntityValidator {

    private final UserEntityRoleRepository userEntityRoleRep;

    // Can operate with an entity only if user has admin rol on entity or has a system admin role
    public boolean canOperateWithEntity(Long entityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // If current user belongs to the entity, he must have the admin role on the entity
        EntityRole entityRole = userEntityRoleRep.getEntityRole(userDetails.getId(), entityId)
                                    .orElseThrow(() -> new EntityNotFoundException(
                                        "El usuario logueado no está asignado a la entidad"
                                    ));
        
        if(entityRole.getName().equals(EntityRoleTypes.ROLE_ADMIN.toString()) || 
            userHasRole(userDetails, RoleTypes.ROLE_ADMIN) ) {
            return true;
        }
        
        return false;
    }

    public boolean currentUserIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return userHasRole(userDetails, RoleTypes.ROLE_ADMIN);
    }

    private boolean userHasRole(CustomUserDetails userDetails, RoleTypes role) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }
}
