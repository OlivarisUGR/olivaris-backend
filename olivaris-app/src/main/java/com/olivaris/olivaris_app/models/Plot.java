package com.olivaris.olivaris_app.models;

import java.util.List;

import org.locationtech.jts.geom.Polygon;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "plot",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"province_code", "city_code", "polygon_code", "plot_num"}
    )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{plot.provinceCode.notblank}")
    @Column(name = "province_code", length = 2, nullable = false)
    private String provinceCode;

    @NotBlank(message = "{plot.province.notblank}")
    @Column(name = "province", nullable = false)
    private String province;

    @NotBlank(message = "{plot.cityCode.notblank}")
    @Column(name = "city_code", length = 3, nullable = false)
    private String cityCode;

    @NotBlank(message = "{plot.city.notblank}")
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "addition_code", length = 2)
    private String additionCode;

    @Column(name = "zone_code", length = 2)
    private String zoneCode;

    @NotBlank(message = "{plot.polygonCode.notblank}")
    @Column(name = "polygon_code", length = 4, nullable = false)
    private String polygonCode;

    @NotBlank(message = "{plot.plotNum.notblank}")
    @Column(name = "plot_num", length = 5, nullable = false)
    private String plotNum;

    @NotBlank(message = "{plot.landRegister.notblank}")
    @Column(name = "land_register", length = 20)
    private String landRegister;

    @Column(name = "area")
    private Double area;

    @Column(name = "geometry", columnDefinition = "geometry(Polygon, 4326)")
    private Polygon geometry;

    @OneToMany(mappedBy = "plot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enclosure> enclosures;

    public Plot(String provinceCode, String cityCode, String polygonCode, String plotNum, 
        String landRegister, Double area, Polygon geometry
    ) {
        this.provinceCode = provinceCode;
        this.cityCode= cityCode;
        this.additionCode = "0";
        this.zoneCode = "0";
        this.polygonCode = polygonCode;
        this.plotNum = plotNum;
        this.landRegister = landRegister;
        this.area = area;
        this.geometry = geometry;
    }

    public Plot(String provinceCode, String cityCode, String polygonCode,
        String plotNum, String landRegister, String province, String city
    ) {
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.polygonCode = polygonCode;
        this.plotNum = plotNum;
        this.landRegister = landRegister;
        this.additionCode = "0";
        this.zoneCode = "0";
        this.province = province;
        this.city = city;
    }
}
