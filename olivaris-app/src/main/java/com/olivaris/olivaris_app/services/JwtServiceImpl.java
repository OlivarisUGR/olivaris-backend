package com.olivaris.olivaris_app.services;

import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.models.CustomUserDetails;

@Service
public class JwtServiceImpl implements JwtService {

    @Override
    public String createToken(CustomUserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createToken'");
    }

    @Override
    public String createRefreshToken(CustomUserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createRefreshToken'");
    }

}
