package com.olivaris.olivaris_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
