package com.olivaris.olivaris_app.clients;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.olivaris.olivaris_app.dto.SigpacGeoJsonResponse;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

// Component that has all the SIGPAC API endpoints calls 
@Component
@AllArgsConstructor
public class SigpacApiClient {

    private final WebClient sigpacApiWebClient;

    // SIGPAC API endpoint to get all the enclosures features from a plot
    public SigpacGeoJsonResponse getPlotEnclosures(
        int pr, 
        int mu, 
        int ag, 
        int zo, 
        int po, 
        int pa
    ) {
        return sigpacApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/servicioconsultassigpac/query/recinfoparc/{pr}/{mu}/{ag}/{zo}/{po}/{pa}.geojson")
                    .build(pr, mu, ag, zo, po, pa)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(
                            new RuntimeException(
                                "Error en la llamada a SIGPAC API: " + response.statusCode() + ", body=" + body
                            )
                        ))
                )
                .bodyToMono(SigpacGeoJsonResponse.class)
                .block();
    }

    // Get the plot land register 
    public String getPlotRefCat(
        int pr, 
        int mu, 
        int ag, 
        int zo, 
        int po, 
        int pa
    ) {
        return sigpacApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/servicioconsultassigpac/query/refcatparcela/{pr}/{mu}/{ag}/{zo}/{po}/{pa}.geojson")
                    .build(pr, mu, ag, zo, po, pa)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(
                            new RuntimeException(
                                "Error en la llamada a SIGPAC API: " + response.statusCode() + ", body=" + body
                            )
                        ))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(this::extractRefCat)
                .block();
    }

    @SuppressWarnings("unchecked")
    private String extractRefCat(Map<String, Object> response) {
        if (response == null) {
            throw new IllegalStateException("La respuesta de SIGPAC no contiene datos");
        }

        List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
        if (features == null || features.isEmpty()) {
            throw new IllegalStateException("La respuesta de SIGPAC no contiene la referencia catastral");
        }

        Map<String, Object> properties = (Map<String, Object>) features.get(0).get("properties");
        if (properties == null) {
            throw new IllegalStateException("La respuesta de SIGPAC no contiene propiedades de la parcela");
        }

        Object refCat = properties.get("referencia_cat");
        if (refCat == null || refCat.toString().isBlank()) {
            throw new IllegalStateException("La respuesta de SIGPAC no contiene la referencia catastral");
        }

        return refCat.toString();
    }

    // Endpoint to get all the provinces and his codes
    public Map<String, Object> getProvinceCodes() {
        return sigpacApiWebClient.get()
                .uri("/codigossigpac/provincia.json")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(
                            new RuntimeException(
                                "Error en la llamada a SIGPAC API: " + response.statusCode() + ", body=" + body
                            )
                        ))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    } 

    // Endpoint to get all cities codes from a specific province
    public Map<String, Object> getCityCodes(String provinceCode) {
        return sigpacApiWebClient.get()
                .uri("/codigossigpac/municipio{cod_prov}.json", provinceCode)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(
                            new RuntimeException(
                                "Error en la llamada a SIGPAC API: " + response.statusCode() + ", body=" + body
                            )
                        ))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    } 
}
