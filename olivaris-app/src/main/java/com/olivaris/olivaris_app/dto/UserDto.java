package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Boolean enabled;
    private List<Role> roles;
    private String nif;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getPhone() != null ? user.getPhone() : "",
            user.getEnabled(),
            user.getRoles(),
            user.getNif()
        );
        
        return dto;
    }
}
