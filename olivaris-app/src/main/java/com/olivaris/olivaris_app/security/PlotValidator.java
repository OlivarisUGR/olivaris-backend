package com.olivaris.olivaris_app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.enums.RoleTypes;

import lombok.AllArgsConstructor;

@Component("plotValidator")
@AllArgsConstructor
public class PlotValidator {

    // Only Admin user and the same userId than current user Id can execute it
    public boolean canDeleteUserToPlot(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return userHasRole(userDetails, RoleTypes.ROLE_ADMIN) || userDetails.getId() == userId;
    }

    private boolean userHasRole(CustomUserDetails userDetails, RoleTypes role) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }
}
