package com.olivaris.olivaris_app.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.clients.SigpacApiClient;
import com.olivaris.olivaris_app.dto.CreatePlot;
import com.olivaris.olivaris_app.dto.PlotDto;
import com.olivaris.olivaris_app.dto.PlotEnclosureDto;
import com.olivaris.olivaris_app.dto.SigpacGeoJsonResponse;
import com.olivaris.olivaris_app.dto.UpdateUserPlot;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.Plot;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserPlot;
import com.olivaris.olivaris_app.repositories.PlotRepository;
import com.olivaris.olivaris_app.repositories.UserPlotRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlotServiceImpl implements PlotService {

    private final PlotRepository plotRep;
    private final EnclosureService enclosureServ;
    private final SigpacApiClient sigpacApiClient;
    private final UserPlotRepository userPlotRep;
    private final UserRepository userRep;

    @Transactional
    @Override
    public ResponseEntity<List<PlotEnclosureDto>> createUserPlot(Long userId, CreatePlot request) {
        String province = request.getProvince().trim().toUpperCase();
        String city = request.getCity().trim().toUpperCase();
        String polygonCode = request.getPolygonCode().trim();
        String plotNum = request.getPlotNum().trim();
        int provinceCode = this.getProvinceCode(province);
        int cityCode = this.getCityCode(city, String.valueOf(provinceCode));
        String landRegister = sigpacApiClient.getPlotRefCat(
            provinceCode,
            cityCode,
            0,
            0,
            Integer.valueOf(polygonCode),
            Integer.valueOf(plotNum)
        );
        
        // Find if the plot is saved on database
        Plot plotDb = new Plot();
        Optional<Plot> optionalPlot = plotRep.findByProvinceAndCityAndPolygonCodeAndPlotNum(
                                                province, city, polygonCode, plotNum
                                            );

        if(optionalPlot.isEmpty()) {
            plotDb = this.createPlotAndEnclosuresWithResolvedGeoData(request, provinceCode, cityCode, landRegister);
        } else {
            plotDb = optionalPlot.get();
            
            if(!landRegister.equals(plotDb.getLandRegister())) {
                plotDb.setLandRegister(landRegister);
                plotDb = plotRep.save(plotDb);
            }
        }

        // Assign the plot to the user that execute the method
        User userDb = userRep.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));

        UserPlot userPlot = new UserPlot(userDb, plotDb, request.getName().toUpperCase());
        userPlotRep.save(userPlot);

        List<UserPlot> userPlotDb = userPlotRep.getUserPlots(userId);
        List<PlotEnclosureDto> plotEncDto = userPlotDb.stream()
            .map(up -> {
                Plot plotDB = up.getPlot();
                String plotName = up.getPlotName();
                return PlotEnclosureDto.fromEntity(plotDB, plotName);
            })
            .toList();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(plotEncDto);
    }

    @Transactional
    @Override
    public Plot createPlotAndEnclosures(CreatePlot request) {
        String province = request.getProvince().trim().toUpperCase();
        String city = request.getCity().trim().toUpperCase();
        String polygonCode = request.getPolygonCode().trim();
        String plotNum = request.getPlotNum().trim();

        int provinceCode = this.getProvinceCode(province);
        int cityCode = this.getCityCode(city, String.valueOf(provinceCode));

        String landRegister = sigpacApiClient.getPlotRefCat(
            provinceCode,
            cityCode,
            0,
            0,
            Integer.valueOf(polygonCode),
            Integer.valueOf(plotNum)
        );

        return this.createPlotAndEnclosuresWithResolvedGeoData(request, provinceCode, cityCode, landRegister);
    }

    @Transactional
    private Plot createPlotAndEnclosuresWithResolvedGeoData(
        CreatePlot request,
        int provinceCode,
        int cityCode,
        String landRegister
    ) {
        String polygonCode = request.getPolygonCode().trim();
        String plotNum = request.getPlotNum().trim();
        String province = request.getProvince().trim().toUpperCase();
        String city = request.getCity().trim().toUpperCase();

        // Create and save the plot
        Plot newPlot = new Plot(
            String.valueOf(provinceCode),
            String.valueOf(cityCode), 
            polygonCode, 
            plotNum, 
            landRegister,
            province,
            city
        );

        Plot plotDb = plotRep.save(newPlot);

        // Get plot enclosures from SIGPAC API
        SigpacGeoJsonResponse plotEnclosures = sigpacApiClient.getPlotEnclosures(
            provinceCode, 
            cityCode,
            0, 
            0, 
            Integer.valueOf(polygonCode), 
            Integer.valueOf(plotNum)
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

        return plotDb;
    }

    @Transactional
    @Override
    public ResponseEntity<Void> delete(Long id) {
        Plot plotDb = plotRep.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                            "La parcela no existe en la base de datos"
                        ));
        
        plotRep.delete(plotDb);
        
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    @PreAuthorize("@plotValidator.canDeleteUserToPlot(#userId)")
    public ResponseEntity<Void> deleteUserPlot(Long plotId, Long userId) {
        UserPlot userPlotDb = userPlotRep.findByUserIdAndPlotId(userId, plotId)
                        .orElseThrow(() -> new EntityNotFoundException(
                            "La relación entre usuario y parcela no existe"
                        ));
        
        userPlotRep.delete(userPlotDb);
        
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<PlotDto> getPlot(Long plotId) {
        Plot plotDb = plotRep.findById(plotId)
            .orElseThrow(() -> new EntityNotFoundException(
                "La parcela no existe en el sistema"
            ));

        PlotDto plotDto = PlotDto.fromEntity(plotDb);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(plotDto);   
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<PlotEnclosureDto> getPlotEnclosures(Long plotId) {
        Plot plotDb = plotRep.findById(plotId)
            .orElseThrow(() -> new EntityNotFoundException(
                "La parcela no existe en el sistema"
            ));
        
        PlotEnclosureDto plotEnclosuresDto = PlotEnclosureDto.fromEntity(plotDb);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(plotEnclosuresDto);   
    }

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("@userValidator.sameUserThanCurrent(#userId)")
    public ResponseEntity<List<PlotEnclosureDto>> getUserPlots(Long userId) {
        User userDb = userRep.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));
        
        List<UserPlot> userPlotDb = userPlotRep.getUserPlots(userId);
        List<PlotEnclosureDto> plotEncDto = userPlotDb.stream()
            .map(up -> {
                Plot plotDb = up.getPlot();
                String plotName = up.getPlotName();
                return PlotEnclosureDto.fromEntity(plotDb, plotName);
            })
            .toList();
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(plotEncDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Map<String, Long>> getEnclosureId(
        String plotName, 
        String enclosureName,
        Long userId
    ) {
        Long enclosureId = userPlotRep.getEnclosureIdByUserIdAndPlotNameAndEnclosureName(
            plotName.toUpperCase(), enclosureName.toUpperCase(), userId)
            .orElseThrow(() -> new EntityNotFoundException(
                "No se ha encontrado el recinto para los parámetros pasados"
            ));

        Map<String, Long> enclosureMap = new HashMap<>();
        enclosureMap.put("enclosureId", enclosureId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(enclosureMap);
    }

    @Override
    public ResponseEntity<PlotDto> updateUserPlot(
        Long plotId, 
        Long userId, 
        UpdateUserPlot request
    ) {
        UserPlot userPlotDb = userPlotRep.findByUserIdAndPlotId(userId, plotId)
            .orElseThrow(() -> new EntityNotFoundException(
                "No existe relación entre el usuario y la entidad"
            ));

        if(!request.getPlotName().equals("")) {
            userPlotDb.setPlotName(request.getPlotName());
            userPlotRep.save(userPlotDb);
        }
        
        if(!request.getLandRegister().equals("")) {
            Plot plotDb = userPlotDb.getPlot();
            plotDb.setLandRegister(request.getLandRegister());
            plotRep.save(plotDb);
        }

        PlotDto plotDto = PlotDto.fromEntity(userPlotDb.getPlot());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(plotDto);
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
