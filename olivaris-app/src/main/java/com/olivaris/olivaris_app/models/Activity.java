package com.olivaris.olivaris_app.models;

import java.time.LocalDate;
import java.util.List;

import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.ActivityType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity",
    uniqueConstraints = @UniqueConstraint(
        name = "unique_constraint_activity",
        columnNames = {"user_id", "enclosure_id", "type", "date"}
    )
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "enclosure_id", nullable = false)
    private Enclosure enclosure;

    @NotNull(message = "{activity.type.notnull}")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @NotNull(message = "{activity.date.notnull}")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(nullable = true)
    private String description;

    @NotBlank(message = "{activity.season.notblank}")
    @Column(nullable = false)
    private String season;

    @NotNull(message = "{activity.status.notnull}")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhytoAct> phytoAct;
}
