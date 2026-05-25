package com.olivaris.olivaris_app.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.LoginRequest;
import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.TokenResponse;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.services.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.HtmlUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping(value = "/api/auth")
@AllArgsConstructor
public class AuthController {

    private static final String CONFIRMATION_TEMPLATE_PATH = "templates/confirmation-result.html";
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerSystemUser(@Valid @RequestBody RegisterRequest request) {
        return authService.registerSystemUser(request);
    }

    @PostMapping("/register/entityAdmin")
    public ResponseEntity<UserDto> registerEntityAdminUser(@Valid @RequestBody RegisterRequest request) {
        return authService.registerEntityAdminUser(request);
    }
    
    @GetMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> confirm(@RequestParam String token) {
        UserDto confirmedUser = authService.confirm(token);
        String safeFirstname = HtmlUtils.htmlEscape(confirmedUser.getFirstname());

        String htmlTemplate = loadConfirmationTemplate();
        String html = htmlTemplate.replace("{{firstname}}", safeFirstname);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .contentType(MediaType.TEXT_HTML)
            .body(html);
    }

    private String loadConfirmationTemplate() {
        ClassPathResource resource = new ClassPathResource(CONFIRMATION_TEMPLATE_PATH);

        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar la plantilla de confirmacion", ex);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return authService.refresh(authHeader);
    }
}
