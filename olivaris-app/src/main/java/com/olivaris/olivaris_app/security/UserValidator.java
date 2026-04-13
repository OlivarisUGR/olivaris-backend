package com.olivaris.olivaris_app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Component("userValidator")
@AllArgsConstructor
public class UserValidator {

    private final EntityRepository entityRep;
    private final UserEntityRoleRepository userEntRoleRep;

    public boolean canDeleteSystemUser(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        
        return userDetails.getId() != id;
    }

    public boolean canRegisterEntityAdminUser(RegisterRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        // Current user has system admin role
        if(userHasRole(userDetails, RoleTypes.ROLE_ADMIN)) {
            return true;
        }

        // Current user has other system role -> can register an entity admin if they belong to 
        // the same entity and current user has admin role
        if(request.getNif() == null) return false;

        EnabledEntity entity = entityRep.findByNif(request.getEntityNif())
            .orElseThrow(() -> new EntityNotFoundException(
                "La entidad habilitada para ese NIF no existe en el sistema"
            ));
            
        boolean userBelongToEntity = userEntRoleRep.userBelongToEntity(currentUserId, entity.getId());    
        boolean userHasAdminRole = userEntRoleRep.userRoleOnEnt(
            EntityRoleTypes.ROLE_ADMIN.toString(), currentUserId, entity.getId());
        
        return userBelongToEntity && userHasAdminRole;
    }

    // Only admin user can get all users
    public boolean canGetSystemUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        return userHasRole(userDetails, RoleTypes.ROLE_ADMIN);
    }

    // Current user has the same id than the userId
    public boolean sameUserThanCurrent(Long userId) {
        // Get the current user logued on system
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = customUser.getId();

        return currentUserId.equals(userId);
    }

    private boolean userHasRole(CustomUserDetails userDetails, RoleTypes role) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }
}
