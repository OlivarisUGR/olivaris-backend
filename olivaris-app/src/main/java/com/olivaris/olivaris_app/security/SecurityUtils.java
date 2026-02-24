package com.olivaris.olivaris_app.security;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.olivaris.olivaris_app.models.enums.RoleTypes;

public class SecurityUtils {

    public static boolean currentUserHasRole(RoleTypes role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.toString()));
    }

    public static boolean requestHasRole(List<RoleTypes> roleList, RoleTypes role) {
        return roleList.stream().anyMatch(r -> r.toString().equals(role.toString()));
    }
}
