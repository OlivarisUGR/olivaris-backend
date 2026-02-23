package com.olivaris.olivaris_app.services;

import com.olivaris.olivaris_app.models.User;

public interface JwtService {
    String buildToken(User user, Long expiration);
    String createToken(User user);
    String createRefreshToken(User user);
    String extractUsername(String token);
    Boolean isValid(String token, User user);
}
