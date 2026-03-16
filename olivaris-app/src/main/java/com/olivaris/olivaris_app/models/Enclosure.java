package com.olivaris.olivaris_app.models;

import org.locationtech.jts.geom.Polygon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enclosure")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Enclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{enclosure.name.notblank}")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "{enclosure.area.notblank}")
    @Column(name = "area", nullable = false)
    private Double area;

    @NotBlank(message = "{enclosure.sigpacUse.notblank}")
    @Column(name = "sigpac_use", nullable = false)
    private String sigpacUse;

    @NotNull(message = "{enclosure.geometry.notnull}")
    @Column(name = "geometry", nullable = false)
    private Polygon geometry;

    @ManyToOne
    @JoinColumn(name = "plot_id", nullable = false)
    private Plot plot;

    public Enclosure(Plot plot, String name, Double area, String sigpacUse, Polygon geometry) {
        this.plot = plot;
        this.name = name;
        this.area = area;
        this.sigpacUse = sigpacUse;
        this.geometry = geometry;
    }
}
