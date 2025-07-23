package com.lulu.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/logging")
public class LoggingController {

    private static final Logger logger = LoggerFactory.getLogger(LoggingController.class);

    /**
     * Endpoint para probar diferentes niveles de logging
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testLogging(@RequestBody Map<String, String> request) {
        String level = request.getOrDefault("level", "INFO");
        String message = request.getOrDefault("message", "Mensaje de prueba");
        
        switch (level.toUpperCase()) {
            case "TRACE":
                logger.trace("TRACE - {}", message);
                break;
            case "DEBUG":
                logger.debug("DEBUG - {}", message);
                break;
            case "INFO":
                logger.info("INFO - {}", message);
                break;
            case "WARN":
                logger.warn("WARN - {}", message);
                break;
            case "ERROR":
                logger.error("ERROR - {}", message);
                break;
            default:
                logger.info("Nivel desconocido, usando INFO - {}", message);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("level", level);
        response.put("message", "Log enviado correctamente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para generar logs de ejemplo con contexto
     */
    @PostMapping("/simulate/{scenario}")
    public ResponseEntity<String> simulateScenario(@PathVariable String scenario) {
        
        switch (scenario.toLowerCase()) {
            case "user-login":
                simulateUserLogin();
                break;
            case "order-process":
                simulateOrderProcess();
                break;
            case "error-handling":
                simulateErrorHandling();
                break;
            case "performance":
                simulatePerformanceLog();
                break;
            default:
                logger.warn("Escenario de simulación desconocido: {}", scenario);
                return ResponseEntity.badRequest().body("Escenario no válido");
        }
        
        return ResponseEntity.ok("Simulación completada: " + scenario);
    }
    
    private void simulateUserLogin() {
        logger.info("=== SIMULACIÓN: LOGIN DE USUARIO ===");
        logger.info("Usuario 'demo@floreria.com' intentando login desde IP: 192.168.1.100");
        logger.debug("Validando credenciales...");
        logger.info("Login exitoso para usuario: demo@floreria.com");
        logger.debug("Token JWT generado con expiración en 24 horas");
    }
    
    private void simulateOrderProcess() {
        logger.info("=== SIMULACIÓN: PROCESAMIENTO DE ORDEN ===");
        logger.info("Iniciando procesamiento de orden para usuario: cliente123");
        logger.debug("Orden contiene 3 productos: [Rosa Roja x2, Girasol x1, Tulipán x3]");
        logger.info("Validando disponibilidad de productos...");
        logger.warn("Producto 'Rosa Roja' tiene stock bajo: 5 unidades restantes");
        logger.info("Aplicando cupón de descuento: PROMO10 (-10%)");
        logger.info("Total de la orden: $85.50 (descuento aplicado: $9.50)");
        logger.info("Orden creada exitosamente con ID: ORD-2025-001234");
    }
    
    private void simulateErrorHandling() {
        logger.info("=== SIMULACIÓN: MANEJO DE ERRORES ===");
        logger.error("Error de conexión a la base de datos detectado");
        logger.warn("Reintentando conexión... Intento 1/3");
        logger.warn("Reintentando conexión... Intento 2/3");
        logger.info("Conexión a base de datos restaurada");
        
        // Simulando un error de validación
        logger.error("Error de validación: Email inválido recibido: 'usuario@email'");
        logger.warn("Cliente intentó registrarse con datos incompletos");
    }
    
    private void simulatePerformanceLog() {
        logger.info("=== SIMULACIÓN: LOGS DE PERFORMANCE ===");
        long startTime = System.currentTimeMillis();
        
        logger.debug("Iniciando consulta compleja de productos...");
        // Simulando tiempo de procesamiento
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Consulta de productos completada en {} ms", duration);
        
        if (duration > 200) {
            logger.warn("Consulta lenta detectada: {} ms (threshold: 200ms)", duration);
        }
    }
}
