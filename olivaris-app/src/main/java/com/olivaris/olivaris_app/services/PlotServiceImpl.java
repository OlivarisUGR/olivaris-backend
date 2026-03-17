package com.olivaris.olivaris_app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.clients.SigpacApiClient;
import com.olivaris.olivaris_app.dto.CreatePlot;
import com.olivaris.olivaris_app.dto.PlotDto;
import com.olivaris.olivaris_app.dto.SigpacGeoJsonResponse;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.Plot;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.repositories.PlotRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlotServiceImpl implements PlotService {

    private final PlotRepository plotRep;
    private final EnclosureService enclosureServ;
    private final SigpacApiClient sigpacApiClient;
    private final UserRepository userRep;

    @Transactional
    @Override
    public ResponseEntity<PlotDto> create(CreatePlot request) {
        int provinceCode = this.getProvinceCode(request.getProvince());
        int cityCode = this.getCityCode(request.getCity(), String.valueOf(provinceCode));

        // Create and save the plot
        Plot newPlot = new Plot(
            request.getName(),
            String.valueOf(provinceCode),
            String.valueOf(cityCode), 
            request.getPolygonCode(), 
            request.getPlotNum(), 
            request.getLandRegister(),
            request.getProvince().toUpperCase(),
            request.getCity().toUpperCase()
        );

        Plot plotDb = plotRep.save(newPlot);

        // Assign the plot to the user that execute the method
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User userDb = userDetails.getUser();

        userDb.getPlotList().add(plotDb);
        userRep.save(userDb);

        // Get plot enclosures from SIGPAC API
        SigpacGeoJsonResponse plotEnclosures = sigpacApiClient.getPlotEnclosures(
            provinceCode, 
            cityCode,
            0, 
            0, 
            Integer.valueOf(request.getPolygonCode()), 
            Integer.valueOf(request.getPlotNum())
        );

        // Create and save the plot enclosures on the system
        List<Map<String, Object>> enclosureCoord = plotEnclosures.getFeatures();
        List<Enclosure> plotEnclosuresList = enclosureServ.createPlotEnclosures(enclosureCoord, plotDb);
        
        // Calculate the area and geometry plot
        List<Polygon> geometries = new ArrayList<>();
        Double area = 0.0;

        for(Enclosure en : plotEnclosuresList) {
            area += en.getArea();
            geometries.add(en.getGeometry());
        }

        if(!geometries.isEmpty()) {
            // Union() method combine all geometries in one geometry
            Geometry combinedGeometry = CascadedPolygonUnion.union(geometries);
            
            if (combinedGeometry instanceof Polygon polygon) {
                plotDb.setGeometry(polygon);
            }
            
            plotDb.setEnclosures(plotEnclosuresList);
            plotDb.setArea(area);
            plotDb = plotRep.save(plotDb);
        }

        // Create the Plot DTO
        PlotDto plotDto = PlotDto.fromEntity(plotDb);
        return ResponseEntity.status(HttpStatus.CREATED).body(plotDto);
    }

    @Transactional
    @Override
    public ResponseEntity<Void> delete(Long id) {
        Plot plotDb = plotRep.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("La parcela no existe en la base de datos"));
        
        plotRep.delete(plotDb);
        
        return ResponseEntity.noContent().build();
    }

    private int getProvinceCode(String province) {
        Map<String, Object> provinces = sigpacApiClient.getProvinceCodes();
        List<Map<String, Object>> provincesList = (List<Map<String, Object>>) provinces.get("codigos");
       
        return provincesList.stream()
            .filter(prMap -> ((String) prMap.get("descripcion")).equals(province.toUpperCase()))
            .map(prMap -> (Integer) prMap.get("codigo"))
            .findFirst()
            .orElse(-1);
    }

    private int getCityCode(String city, String provinceCode) {
        Map<String, Object> cities = sigpacApiClient.getCityCodes(provinceCode);
        List<Map<String, Object>> citiesList = (List<Map<String, Object>>) cities.get("codigos");

        return citiesList.stream()
            .filter(ctMap -> {
                String cityMap = (String) ctMap.get("descripcion");
                return cityMap.toUpperCase().equals(city.toUpperCase());
            })
            .map(ctMap -> (Integer) ctMap.get("codigo"))
            .findFirst()
            .orElse(-1);
    }
}
