package com.lulu.logging.utils;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Utilidades para logging estructurado y contextual
 */
public class LogUtils {

    /**
     * Logs de eventos de negocio importantes
     */
    public static class BusinessEvent {
        
        public static void userRegistered(Logger logger, String username, String email, String role) {
            logger.info("BUSINESS_EVENT=USER_REGISTERED | username={} | email={} | role={}", 
                       username, email, role);
        }
        
        public static void userLogin(Logger logger, String username, String ip, boolean successful) {
            if (successful) {
                logger.info("BUSINESS_EVENT=USER_LOGIN_SUCCESS | username={} | ip={}", username, ip);
            } else {
                logger.warn("BUSINESS_EVENT=USER_LOGIN_FAILED | username={} | ip={}", username, ip);
            }
        }
        
        public static void orderCreated(Logger logger, String orderId, String username, 
                                       double total, int itemCount) {
            logger.info("BUSINESS_EVENT=ORDER_CREATED | orderId={} | username={} | total=${} | items={}", 
                       orderId, username, total, itemCount);
        }
        
        public static void orderCancelled(Logger logger, String orderId, String username, String reason) {
            logger.warn("BUSINESS_EVENT=ORDER_CANCELLED | orderId={} | username={} | reason={}", 
                       orderId, username, reason);
        }
        
        public static void paymentProcessed(Logger logger, String orderId, String paymentId, 
                                          double amount, String status) {
            logger.info("BUSINESS_EVENT=PAYMENT_PROCESSED | orderId={} | paymentId={} | amount=${} | status={}", 
                       orderId, paymentId, amount, status);
        }
        
        public static void productCreated(Logger logger, String productName, String category, double price) {
            logger.info("BUSINESS_EVENT=PRODUCT_CREATED | product={} | category={} | price=${}", 
                       productName, category, price);
        }
        
        public static void lowStock(Logger logger, String productName, int currentStock, int threshold) {
            logger.warn("BUSINESS_EVENT=LOW_STOCK_ALERT | product={} | currentStock={} | threshold={}", 
                       productName, currentStock, threshold);
        }
    }

    /**
     * Logs de performance y métricas
     */
    public static class Performance {
        
        public static void logExecutionTime(Logger logger, String operation, long durationMs) {
            if (durationMs > 1000) {
                logger.warn("PERFORMANCE=SLOW_OPERATION | operation={} | duration={}ms", operation, durationMs);
            } else {
                logger.debug("PERFORMANCE=OPERATION_COMPLETED | operation={} | duration={}ms", operation, durationMs);
            }
        }
        
        public static void logDatabaseQuery(Logger logger, String query, long durationMs, int resultCount) {
            if (durationMs > 500) {
                logger.warn("PERFORMANCE=SLOW_QUERY | query={} | duration={}ms | results={}", 
                           query, durationMs, resultCount);
            } else {
                logger.debug("PERFORMANCE=QUERY_EXECUTED | query={} | duration={}ms | results={}", 
                            query, durationMs, resultCount);
            }
        }
        
        public static void logApiCall(Logger logger, String endpoint, String method, 
                                     long durationMs, int statusCode) {
            String level = statusCode >= 400 ? "ERROR" : durationMs > 2000 ? "WARN" : "INFO";
            
            if ("ERROR".equals(level)) {
                logger.error("PERFORMANCE=API_CALL | endpoint={} | method={} | duration={}ms | status={}", 
                           endpoint, method, durationMs, statusCode);
            } else if ("WARN".equals(level)) {
                logger.warn("PERFORMANCE=SLOW_API_CALL | endpoint={} | method={} | duration={}ms | status={}", 
                          endpoint, method, durationMs, statusCode);
            } else {
                logger.info("PERFORMANCE=API_CALL | endpoint={} | method={} | duration={}ms | status={}", 
                          endpoint, method, durationMs, statusCode);
            }
        }
    }

    /**
     * Logs de seguridad
     */
    public static class Security {
        
        public static void unauthorizedAccess(Logger logger, String endpoint, String ip, String userAgent) {
            logger.warn("SECURITY=UNAUTHORIZED_ACCESS | endpoint={} | ip={} | userAgent={}", 
                       endpoint, ip, userAgent);
        }
        
        public static void suspiciousActivity(Logger logger, String username, String activity, String details) {
            logger.warn("SECURITY=SUSPICIOUS_ACTIVITY | username={} | activity={} | details={}", 
                       username, activity, details);
        }
        
        public static void jwtTokenExpired(Logger logger, String username, String endpoint) {
            logger.info("SECURITY=JWT_EXPIRED | username={} | endpoint={}", username, endpoint);
        }
        
        public static void rateLimitExceeded(Logger logger, String ip, String endpoint, int attempts) {
            logger.warn("SECURITY=RATE_LIMIT_EXCEEDED | ip={} | endpoint={} | attempts={}", 
                       ip, endpoint, attempts);
        }
    }

    /**
     * Utilidades para contexto MDC
     */
    public static class Context {
        
        public static void setUserContext(String userId, String username) {
            MDC.put("userId", userId);
            MDC.put("username", username);
        }
        
        public static void setOrderContext(String orderId) {
            MDC.put("orderId", orderId);
        }
        
        public static void setTransactionContext(String transactionId) {
            MDC.put("transactionId", transactionId);
        }
        
        public static void addCustomContext(String key, String value) {
            MDC.put(key, value);
        }
        
        public static void addCustomContext(Map<String, String> contextMap) {
            contextMap.forEach(MDC::put);
        }
        
        public static void clearUserContext() {
            MDC.remove("userId");
            MDC.remove("username");
        }
        
        public static void clearOrderContext() {
            MDC.remove("orderId");
        }
        
        public static void clearAllContext() {
            MDC.clear();
        }
    }

    /**
     * Logs de errores estructurados
     */
    public static class Error {
        
        public static void logException(Logger logger, String operation, Exception e) {
            logger.error("ERROR=EXCEPTION | operation={} | exception={} | message={}", 
                        operation, e.getClass().getSimpleName(), e.getMessage(), e);
        }
        
        public static void logValidationError(Logger logger, String field, String value, String reason) {
            logger.warn("ERROR=VALIDATION | field={} | value={} | reason={}", field, value, reason);
        }
        
        public static void logBusinessRuleViolation(Logger logger, String rule, String context) {
            logger.warn("ERROR=BUSINESS_RULE_VIOLATION | rule={} | context={}", rule, context);
        }
        
        public static void logExternalServiceError(Logger logger, String service, String operation, 
                                                 int statusCode, String message) {
            logger.error("ERROR=EXTERNAL_SERVICE | service={} | operation={} | status={} | message={}", 
                        service, operation, statusCode, message);
        }
    }
}
