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

    public boolean canCreateUpdateAssignment(Long entityId) {
        boolean isCurrentAdmin = this.currentUserHasRole(RoleTypes.ROLE_ADMIN);
        boolean isCurrentEntityAdmin = this.currentUserHasRole(RoleTypes.ROLE_ENTITY_ADMIN);

        // Only user with Admin or EntityAdmin rol can access
        if(!isCurrentAdmin || !isCurrentEntityAdmin) {
            return false;
        }

        // If current user is an entity admin, he must have the admin role on the entity
        if(isCurrentEntityAdmin && !isCurrentAdmin) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            EntityRole entityRole = userEntityRoleRep.getEntityRole(userDetails.getId(), entityId)
                                                        .orElseThrow(() -> new EntityNotFoundException(
                                                            "No se ha encontrado el usuario asignado a la entidad"
                                                        ));
            
            // Only user that has ADMIN role on the entity can access
            if(!entityRole.getName().equals(EntityRoleTypes.ROLE_ADMIN.toString())) {
                return false;
            }
        }
        
        return true;
    }

    private boolean currentUserHasRole(RoleTypes role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }
}
