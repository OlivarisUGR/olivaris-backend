package com.olivaris.olivaris_app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_plot",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "plot_id"}
    )
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "plot_id", nullable = false)
    private Plot plot;

    @NotBlank(message = "{plot.name.notblank}")
    @Column(name = "plot_name", nullable = false)
    private String plotName;

    public UserPlot(User user, Plot plot, String plotName) {
        this.user = user;
        this.plot = plot;
        this.plotName = plotName;
    }
}
