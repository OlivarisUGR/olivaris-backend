package com.olivaris.olivaris_app;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.repositories.UserRepository;
import com.olivaris.olivaris_app.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

// This component will execute the doFilterInternal() method for each request before 
// execute the security filter chain. It will be executed when an authenticated user try
// to access to some endpoint
@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRep;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {
        // If the request is to /auth ... endpoints, JWT won't be validate (skip filters)
        if(request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get header and check his format
        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get the user from token and check if exists
        String token = header.substring(7);
        String userEmail = jwtService.extractUsername(token);

        if(userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> optionalUser = userRep.findByEmail(userEmail);

        if(optionalUser.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if the token is valid (valid for the user and it has not expired)
        User userDb = optionalUser.get();

        if(!jwtService.isValid(token, userDb)) {
            filterChain.doFilter(request, response);
            return;
        }

        // All right -> Accept the request
        // Create an authenticated user for Spring and save it on security context
        CustomUserDetails userDetails = new CustomUserDetails(userDb);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

        // Save request info like IP, session, etc
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Save the request context
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
