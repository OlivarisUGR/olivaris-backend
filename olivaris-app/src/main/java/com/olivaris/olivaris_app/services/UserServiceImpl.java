package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.models.ConfirmationToken;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.ConfirmationTokenRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;


@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRep;
    private final ConfirmationTokenRepository confirmTokenRep;
    private final Long confirmTokenExpiresHours;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String urlMailHost;
    private final RoleRepository roleRep;

    // Using this constructor because @Value does not injected automatically by Springboot
    // if @AllArgsConstructor is used
    public UserServiceImpl(UserRepository userRep,
                            ConfirmationTokenRepository confirmTokenRep,
                            @Value("${confirmation-token.expiration-hours}") Long confirmTokenExpiresHours,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService,
                            @Value("${spring.mail.urlServerHost}") String urlMailHost,
                            RoleRepository roleRep
    ) {
        this.userRep = userRep;
        this.confirmTokenRep = confirmTokenRep;
        this.confirmTokenExpiresHours = confirmTokenExpiresHours;
        this.passwordEncoder = passwordEncoder;     
        this.emailService = emailService;    
        this.urlMailHost = urlMailHost;    
        this.roleRep = roleRep;       
    }

    @Transactional
    @Override
    public User register(RegisterRequest request) {
        // Create the user and save him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        List<Role> roles = new ArrayList<>();
        
        // Find the role user and save on the list
        Optional<Role> optionalRole = roleRep.findByName(RoleTypes.ROLE_USER.toString());
        
        if(optionalRole.isEmpty()) {
            throw new RoleNotExistsException(RoleTypes.ROLE_USER.toString());
        }

        roles.add(optionalRole.get());

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            phone,
            roles,
            false
        );

        User savedUser = userRep.save(newUser);

        // Create the confirm token and save it
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(confirmTokenExpiresHours);
        ConfirmationToken confirmToken = new ConfirmationToken(token, expiresAt, savedUser, true);
        confirmTokenRep.save(confirmToken);

        // Send the email confirmation
        String url = urlMailHost + token;
        emailService.sendEmail(savedUser.getEmail(), url);

        return savedUser;
    }
}
