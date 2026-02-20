package com.olivaris.olivaris_app.services;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.models.User;

public interface UserService {
    User register(RegisterRequest request);
}
