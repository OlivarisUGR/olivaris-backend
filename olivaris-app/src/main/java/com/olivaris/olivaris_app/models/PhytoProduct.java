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
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phyto_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PhytoProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{phytoProduct.name.notblank}")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "{phytoProduct.oficialRegister.notblank}")
    @Column(name = "oficial_register", nullable = false, unique = true)
    private String oficialRegister;

    @NotBlank(message = "{phytoProduct.activeSubstance.notblank}")
    @Column(name = "active_substance", nullable = false)
    private String activeSubstance;

    @NotBlank(message = "{phytoProduct.type.notblank}")
    @Column(nullable = false)
    private String type;

    @OneToMany(mappedBy = "phytoProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhytoAct> activities;
}
