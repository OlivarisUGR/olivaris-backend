package com.olivaris.olivaris_app.models;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "confirmation_token")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull
    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public ConfirmationToken(String token, LocalDateTime expiresAt, User user, Boolean active) {
        this.token = token;
        this.active = true;
        this.expiresAt = expiresAt;
        this.user = user;
        this.active = active;
    }
}
