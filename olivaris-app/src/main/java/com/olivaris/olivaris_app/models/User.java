package com.olivaris.olivaris_app.models;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_data")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{user.firstname.notblank}")
    @Column(nullable = false)
    private String firstname;

    @NotBlank(message = "{user.lastname.notblank}")
    @Column(nullable = false)
    private String lastname;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "{user.password.notblank}")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "{user.phone.invalid}")
    private String phone;

    // Hibernate will create and inserted the value automatically
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Hibernate will create and inserted the value automatically
    @UpdateTimestamp
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean enabled;

    // This will generate a relationship table between user table and role table
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "role_id"}) }
    )
    private List<Role> roles;

    public User(String firstname, String lastname, String email, String password, 
        String phone, List<Role> roles, Boolean enabled
    ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.roles = roles;
        this.enabled = enabled;
    }
}
