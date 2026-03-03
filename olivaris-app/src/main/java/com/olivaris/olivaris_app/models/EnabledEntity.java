package com.olivaris.olivaris_app.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enabled_entity")
@Getter
@Setter
@NoArgsConstructor
public class EnabledEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{entity.name.notblank}")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "{entity.nif.notblank}")
    @Pattern(regexp = "^[ABCDEFGHJKLMNPQRSUVW]{1}[0-9]{7}[0-9A-J]{1}$", message = "{entity.nif.invalid}")
    @Column(unique = true, nullable = false)
    private String nif;

    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "{entity.phone.invalid}")
    private String phone;

    @NotBlank(message = "{entity.email.notblank}")
    @Email(message = "{entity.email.invalid}")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "enabledEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntityRole> userEntity;

    public EnabledEntity(String name, String nif, String phone, String email, Boolean active) {
        this.name = name;
        this.nif = nif;
        this.phone = phone;
        this.email = email;
        this.active = active;
    }
}
