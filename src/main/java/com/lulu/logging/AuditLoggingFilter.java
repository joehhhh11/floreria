package com.lulu.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class AuditLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingFilter.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Generar ID único para la request
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        // Usar MDC (Mapped Diagnostic Context) para contexto de logs
        MDC.put("requestId", requestId);
        MDC.put("userIP", getClientIpAddress(request));
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Log de entrada
            logRequest(request, requestId);
            
            filterChain.doFilter(request, response);
            
            // Log de salida
            long duration = System.currentTimeMillis() - startTime;
            logResponse(request, response, requestId, duration);
            
        } finally {
            // Limpiar MDC
            MDC.clear();
        }
    }
    
    private void logRequest(HttpServletRequest request, String requestId) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        
        // Log básico para todas las requests
        logger.info("REQUEST [{}] {} {}{}", 
                   requestId, method, uri, 
                   queryString != null ? "?" + queryString : "");
        
        // Log de auditoría para endpoints críticos
        if (isCriticalEndpoint(uri)) {
            auditLogger.info("AUDIT_REQUEST [{}] {} {} | IP: {} | UserAgent: {} | Referer: {}", 
                           requestId, method, uri, 
                           MDC.get("userIP"), userAgent, referer);
        }
        
        // Log especial para endpoints de autenticación
        if (isAuthEndpoint(uri)) {
            auditLogger.info("AUTH_ATTEMPT [{}] {} | IP: {}", 
                           requestId, uri, MDC.get("userIP"));
        }
    }
    
    private void logResponse(HttpServletRequest request, HttpServletResponse response, 
                           String requestId, long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        
        // Log de respuesta con métricas de performance
        if (duration > 1000) {
            logger.warn("SLOW_REQUEST [{}] {} {} responded {} in {} ms (SLOW)", 
                       requestId, method, uri, status, duration);
        } else if (status >= 400) {
            logger.warn("ERROR_RESPONSE [{}] {} {} responded {} in {} ms", 
                       requestId, method, uri, status, duration);
        } else {
            logger.info("RESPONSE [{}] {} {} responded {} in {} ms", 
                       requestId, method, uri, status, duration);
        }
        
        // Auditoría de respuestas críticas
        if (isCriticalEndpoint(uri)) {
            auditLogger.info("AUDIT_RESPONSE [{}] {} {} | Status: {} | Duration: {} ms", 
                           requestId, method, uri, status, duration);
        }
        
        // Auditoría de errores de autenticación
        if (isAuthEndpoint(uri) && status >= 400) {
            auditLogger.warn("AUTH_FAILURE [{}] {} | Status: {} | IP: {} | Duration: {} ms", 
                           requestId, uri, status, MDC.get("userIP"), duration);
        } else if (isAuthEndpoint(uri) && status < 300) {
            auditLogger.info("AUTH_SUCCESS [{}] {} | IP: {} | Duration: {} ms", 
                           requestId, uri, MDC.get("userIP"), duration);
        }
    }
    
    private boolean isCriticalEndpoint(String uri) {
        return uri.contains("/api/orders") || 
               uri.contains("/api/payment") || 
               uri.contains("/api/admin") ||
               uri.contains("/api/products") && !uri.endsWith("/api/products"); // No GET all products
    }
    
    private boolean isAuthEndpoint(String uri) {
        return uri.contains("/api/auth/");
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            // X-Forwarded-For puede contener múltiples IPs
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // No filtrar recursos estáticos y health checks
        return path.startsWith("/uploads/") || 
               path.startsWith("/static/") || 
               path.equals("/actuator/health") ||
               path.endsWith(".css") || 
               path.endsWith(".js") || 
               path.endsWith(".ico");
    }
}
