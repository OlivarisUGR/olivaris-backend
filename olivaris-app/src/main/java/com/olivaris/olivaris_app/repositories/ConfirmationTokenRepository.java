package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.ConfirmationToken;
import com.olivaris.olivaris_app.models.User;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
    
    Optional<User> findByUser(int userId);
}
