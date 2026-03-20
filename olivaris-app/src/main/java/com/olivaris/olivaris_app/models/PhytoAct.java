package com.olivaris.olivaris_app.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phyto_activity")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PhytoAct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phyto_product_id", nullable = false)
    private PhytoProduct phytoProduct;

    @NotNull(message = "{phytoAct.date.notnull}")
    @Column(name = "app_date", nullable = false)
    private LocalDate applicationDate;

    @Column(nullable = true)
    private String reason;

    @Column(nullable = true)
    private Double dose;

    // Siempre trabajan con mismo unidad ??
    @Column(name = "dose_unit", nullable = true)
    private String doseUnit;

    @Column(name = "total_amount", nullable = true)
    private Double totalAmount;

    @Column(name = "app_method", nullable = true)
    private String applicationMethod;

    @Column(name = "app_mach", nullable = true)
    private String applicationMachinery;

    @Column(name = "app_name", nullable = true)
    private String applicatorName;

    @Column(name = "app_nif", nullable = true)
    private String applicatorNif;

    @NotBlank(message = "{phytoAct.crops.notblank}")
    @Column(nullable = false)
    private String crops;

    @Column(nullable = true)
    private Double area;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
