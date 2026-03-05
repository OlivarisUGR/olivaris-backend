package com.olivaris.olivaris_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateEntity {

    @NotBlank(message = "{entity.name.notblank}")
    private String name;

    @NotBlank(message = "{entity.nif.notblank}")
    @Pattern(regexp = "^[ABCDEFGHJKLMNPQRSUVW]{1}[0-9]{7}[0-9A-J]{1}$", message = "{entity.nif.invalid}")
    private String nif;

    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "{entity.phone.invalid}")
    private String phone;

    @NotBlank(message = "{entity.email.notblank}")
    @Email(message = "{entity.email.invalid}")
    private String email;
}
