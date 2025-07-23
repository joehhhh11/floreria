package com.lulu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(name = "🏠 Sistema", description = "Endpoints del sistema y estado del servidor")
public class WelcomeController {

    @GetMapping("/")
    @Operation(summary = "Página de bienvenida", description = "Endpoint principal que muestra información general de la API y endpoints disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información de bienvenida obtenida exitosamente")
    })
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "🌸 Bienvenido a Floreria API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("swagger", "https://web-production-98e3.up.railway.app/swagger-ui/index.html");
        response.put("endpoints", Map.of(
            "products", "/api/products",
            "categories", "/api/category",
            "auth", "/api/auth",
            "orders", "/api/orders",
            "users", "/api/users"
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Estado del servidor", description = "Verifica el estado de salud del servidor API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servidor funcionando correctamente")
    })
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Floreria API is running successfully");
        status.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }
}
