package com.olivaris.olivaris_app.services;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    private final String jwtSecretKey;
    private final Long tokenExpiration;
    private final Long refreshTokenExpiration;

    public JwtServiceImpl(
        @Value("${security.jwt.secret-key}") String jwtSecretKey,
        @Value("${security.jwt.token.expiration}") Long tokenExpiration,
        @Value("${security.jwt.refresh-token.expiration}") Long refreshTokenExpiration
    ) {
        this.jwtSecretKey = jwtSecretKey;
        this.tokenExpiration = tokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public String buildToken(User user, Long expiration) {
        return Jwts.builder()
                .id(user.getId().toString())
                .claims(Map.of(
                    "firstname", user.getFirstname(),
                    "userId", user.getId()
                ))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    @Override
    public String createToken(User user) {
        return buildToken(user, this.tokenExpiration);
    }

    @Override
    public String createRefreshToken(User user) {
        return buildToken(user, this.refreshTokenExpiration);
    }

    @Override
    public String extractUsername(String token) {
        Claims jwtToken = Jwts.parser()
                            .verifyWith(getSignKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
        
        return jwtToken.getSubject(); 
    }

    @Override
    public Boolean isValid(String token, User user) {
        String userEmail = extractUsername(token);
        return userEmail.equals(user.getEmail()) && !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                            .verifyWith(getSignKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getExpiration();

        return expiration.before(new Date());
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
