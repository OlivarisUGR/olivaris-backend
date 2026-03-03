package com.olivaris.olivaris_app.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Component("userSecurityValidator")
@AllArgsConstructor
public class UserSecurityValidator {

    private final UserRepository userRep;

    // Those methods will be used with @PreAuthorize
    // If this method returns true -> will execute the next method
    // If this method returns false -> throw an exception automatically

    public boolean canCreateUser(RegisterRequest request) {
        boolean creatingAdmin = this.requestHasRole(request.getRoles(), RoleTypes.ROLE_ADMIN);
        boolean creatingCooperAdmin = this.requestHasRole(request.getRoles(), RoleTypes.ROLE_ENTITY_ADMIN);
        boolean creatingFarmer = this.requestHasRole(request.getRoles(), RoleTypes.ROLE_FARMER);

        if(creatingAdmin) {
            return this.currentUserHasRole(RoleTypes.ROLE_ADMIN);
        }

        if(creatingCooperAdmin || creatingFarmer) {
            return this.currentUserHasRole(RoleTypes.ROLE_ADMIN) || 
                   this.currentUserHasRole(RoleTypes.ROLE_ENTITY_ADMIN);
        }

        return true;
    }

    public boolean canDeleteUser(Long idUserToDelete) {
        // An user can not be deleted by the same user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

        if(currentUser.getId().equals(idUserToDelete)) {
            return false;
        }

        // Find the user to delete and get his roles
        User userToDelete = userRep.findById(idUserToDelete)
                        .orElseThrow(() -> new UserNotFoundException(idUserToDelete.toString()));
        Set<String> deleteUserRoles = userToDelete.getRoles().stream()
                                        .map(r -> r.getName())
                                        .collect(Collectors.toSet());

        // Admin can be deleted by other admin
        if(deleteUserRoles.contains(RoleTypes.ROLE_ADMIN.toString())) {
            return this.currentUserHasRole(RoleTypes.ROLE_ADMIN);
        }

        // Cooper admin can be deleted by other cooper admin or admin
        // Farmer can be deleted by admin or cooper admin; not by other farmer
        if(deleteUserRoles.contains(RoleTypes.ROLE_ENTITY_ADMIN.toString()) ||
            deleteUserRoles.contains(RoleTypes.ROLE_FARMER.toString())) {
            return this.currentUserHasRole(RoleTypes.ROLE_ADMIN) || 
                   this.currentUserHasRole(RoleTypes.ROLE_ENTITY_ADMIN); 
        }
        
        return true;
    }

    private boolean currentUserHasRole(RoleTypes role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }

    private boolean requestHasRole(List<RoleTypes> roleList, RoleTypes role) {
        return roleList.stream().anyMatch(r -> r.toString().equals(role.toString()));
    }
}
