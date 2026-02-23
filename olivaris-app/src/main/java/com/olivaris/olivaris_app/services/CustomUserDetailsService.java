package com.olivaris.olivaris_app.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRep;

    // This method is the way to tell Springboot how can search an user on database when
    // he does an authentication (method authenticate() in login service)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userDb = userRep.findByEmail(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        
        return new CustomUserDetails(userDb);
    }
}
