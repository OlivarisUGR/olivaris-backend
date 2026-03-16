package com.olivaris.olivaris_app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.Plot;
import com.olivaris.olivaris_app.repositories.EnclosureRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EnclosureServiceImpl implements EnclosureService {

    private final EnclosureRepository enclosureRep;

    @Transactional
    @Override
    public List<Enclosure> createPlotEnclosures(List<Map<String, Object>> enclosuresCoord, Plot plot) {
        List<Enclosure> plotEnclosuresList = new ArrayList<>();
        int i = 1;

        // Create each enclosure and save on database
        for(Map<String, Object> enclosureMap : enclosuresCoord) {
            Map<String, Object> geometry = (Map<String, Object>) enclosureMap.get("geometry");
            List<List<Object>> coordinates = (List<List<Object>>) geometry.get("coordinates");
            
            Map<String, Object> properties = (Map<String, Object>) enclosureMap.get("properties");
            Double area = ((Number) properties.get("superficie")).doubleValue();
            String use = properties.get("uso_sigpac").toString();

            Polygon polygon = this.convertToPolygon(coordinates);
            String name = "Recinto " + i;
            i++;

            Enclosure enclosure = new Enclosure(
                plot,
                name,
                area,
                use,
                polygon
            );

            plotEnclosuresList.add(enclosure);
            enclosureRep.save(enclosure);
        }

        return plotEnclosuresList;
    }

    // Function that creates a polygon from each coordinate points
    private Polygon convertToPolygon(List<List<Object>> coordinates) {
        // Coordinates param -> [[ [lon, lat], ... ]]
        List<Object> coordList = coordinates.get(0); 
        Coordinate[] coords = new Coordinate[coordList.size()];

        for (int i = 0; i < coordList.size(); i++) {
            List<Number> point = (List<Number>) coordList.get(i);
            double lon = point.get(0).doubleValue();
            double lat = point.get(1).doubleValue();
            coords[i] = new Coordinate(lon, lat);
        }

        GeometryFactory factory = new GeometryFactory();
        LinearRing shell = factory.createLinearRing(coords);
        return factory.createPolygon(shell, null);
    }
}
