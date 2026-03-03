package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.enums.RoleTypes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "{user.firstname.notblank}")
    private String firstname;

    @NotBlank(message = "{user.lastname.notblank}")
    private String lastname;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{user.password.notblank}")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9]).{9,}$", 
        message = "{user.password.invalid}"
    )
    private String password;
    
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "{user.phone.invalid}")
    private String phone;

    @NotBlank(message = "{user.nif.notblank}")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$")
    private String nif;

    private List<RoleTypes> roles;
}
