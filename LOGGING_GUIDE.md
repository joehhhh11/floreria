# 📊 Sistema de Logging Profesional - Florería Virtual

## 📋 Descripción General

Este proyecto implementa un **sistema de logging profesional y completo** diseñado para proporcionar visibilidad total sobre el comportamiento de la aplicación de florería virtual. El sistema incluye logging estructurado, auditoría, métricas de performance y manejo de errores.

## 🏗️ Arquitectura del Sistema de Logging

### Componentes Principales

1. **Loggers Categorizados**
   - **Application Logs**: Logs generales de la aplicación
   - **Audit Logs**: Logs de auditoría y seguridad
   - **Performance Logs**: Métricas de rendimiento
   - **Error Logs**: Errores críticos y excepciones

2. **Filtros y Interceptores**
   - **AuditLoggingFilter**: Auditoría de requests/responses
   - **FilterConfig**: Configuración de filtros HTTP
   - **MDC Context**: Contexto distribuido para trazabilidad

3. **Utilidades Estructuradas**
   - **LogUtils**: Helpers para logging consistente
   - **Eventos de Negocio**: Logs de operaciones críticas
   - **Performance Monitoring**: Seguimiento de rendimiento

## 📁 Estructura de Archivos de Log

```
logs/
├── app.log                    # Log principal de aplicación
├── app.2025-07-22.log        # Logs rotados por fecha
├── error.log                  # Solo errores críticos
├── audit.log                  # Logs de auditoría y seguridad
├── performance.log            # Requests lentas y métricas
└── audit.2025-07-22.log      # Auditoría rotada
```

## 🔧 Configuración por Entornos

### Desarrollo (dev)
```xml
<logger name="com.lulu" level="DEBUG"/>
<root level="DEBUG">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</root>
```

### Producción (prod)
```xml
<logger name="com.lulu" level="INFO"/>
<root level="INFO">
    <appender-ref ref="FILE"/>
    <appender-ref ref="ERROR_FILE"/>
</root>
```

## 📊 Tipos de Logs Implementados

### 1. Business Events (Eventos de Negocio)
```java
// Registro de usuario
LogUtils.BusinessEvent.userRegistered(logger, username, email, role);

// Login exitoso/fallido
LogUtils.BusinessEvent.userLogin(logger, username, ip, successful);

// Creación de orden
LogUtils.BusinessEvent.orderCreated(logger, orderId, username, total, itemCount);
```

### 2. Performance Monitoring
```java
// Operaciones lentas
LogUtils.Performance.logExecutionTime(logger, "createOrder", durationMs);

// Queries de base de datos
LogUtils.Performance.logDatabaseQuery(logger, query, durationMs, resultCount);

// Llamadas API
LogUtils.Performance.logApiCall(logger, endpoint, method, durationMs, statusCode);
```

### 3. Security & Audit
```java
// Acceso no autorizado
LogUtils.Security.unauthorizedAccess(logger, endpoint, ip, userAgent);

// JWT expirado
LogUtils.Security.jwtTokenExpired(logger, username, endpoint);

// Actividad sospechosa
LogUtils.Security.suspiciousActivity(logger, username, activity, details);
```

### 4. Error Handling
```java
// Excepciones
LogUtils.Error.logException(logger, "createProduct", exception);

// Errores de validación
LogUtils.Error.logValidationError(logger, field, value, reason);

// Servicios externos
LogUtils.Error.logExternalServiceError(logger, service, operation, status, message);
```

## 🎯 Contexto Distribuido con MDC

### Request ID Tracking
```java
// Cada request tiene un ID único
[a1b2c3d4] INFO  com.lulu.orders.service.OrderServiceImpl - Creando orden...
[a1b2c3d4] WARN  com.lulu.payment.PaymentService - Error en pago...
[a1b2c3d4] INFO  com.lulu.orders.service.OrderServiceImpl - Orden completada
```

### User Context
```java
// Contexto de usuario en logs
LogUtils.Context.setUserContext(userId, username);
// Logs automáticamente incluyen: [userId:123] [username:juan.perez]
```

## 🚨 Alertas y Monitoring

### Requests Lentas (> 1000ms)
```
2025-07-22 10:30:15 WARN [a1b2c3d4] SLOW_REQUEST [GET /api/products] responded 200 in 1250 ms
```

### Errores 4xx/5xx
```
2025-07-22 10:30:15 ERROR [b2c3d4e5] ERROR_RESPONSE [POST /api/orders] responded 500 in 45 ms
```

### Login Failures
```
2025-07-22 10:30:15 WARN AUDIT - AUTH_FAILURE [POST /api/auth/login] Status: 401 | IP: 192.168.1.100
```

## 🔍 Patrones de Log Estructurados

### Formato Estándar
```
YYYY-MM-DD HH:mm:ss.SSS [thread] LEVEL [requestId] logger.class - message
```

### Ejemplo Real
```
2025-07-22 14:30:25.123 [http-nio-8080-exec-1] INFO [a1b2c3d4] c.l.orders.service.OrderServiceImpl - BUSINESS_EVENT=ORDER_CREATED | orderId=ORD-001 | username=cliente@email.com | total=$85.50 | items=3
```

## 📈 Métricas y KPIs

### Performance KPIs
- **Response Time**: Tiempo promedio de respuesta por endpoint
- **Slow Queries**: Consultas DB > 500ms
- **Error Rate**: Porcentaje de requests con error
- **Throughput**: Requests por segundo

### Business KPIs
- **User Registrations**: Registros por día/hora
- **Login Success Rate**: Tasa de logins exitosos
- **Order Completion Rate**: Órdenes completadas vs iniciadas
- **Payment Success Rate**: Pagos exitosos

## 🛠️ Testing del Sistema de Logging

### Endpoint de Testing
```bash
# Probar diferentes niveles
POST /api/admin/logging/test
{
  "level": "INFO",
  "message": "Mensaje de prueba"
}

# Simular escenarios
POST /api/admin/logging/simulate/user-login
POST /api/admin/logging/simulate/order-process
POST /api/admin/logging/simulate/error-handling
POST /api/admin/logging/simulate/performance
```

## 📋 Checklist de Implementación

- ✅ **Configuración Logback** - Múltiples appenders y rotación
- ✅ **Filtros de Auditoría** - Tracking completo de requests
- ✅ **Logging Estructurado** - Formato consistente y parseable
- ✅ **Contexto MDC** - Trazabilidad entre componentes
- ✅ **Utils de Logging** - Helpers para eventos comunes
- ✅ **Separación por Tipo** - Archivos específicos por categoría
- ✅ **Configuración por Entorno** - Dev vs Prod
- ✅ **Performance Monitoring** - Tracking de operaciones lentas
- ✅ **Security Audit** - Logs de seguridad y acceso
- ✅ **Error Handling** - Captura estructurada de errores

## 🚀 Beneficios del Sistema

1. **Debugging Eficiente**: Trazabilidad completa de requests
2. **Monitoring Proactivo**: Detección temprana de problemas
3. **Auditoría Completa**: Cumplimiento y seguridad
4. **Performance Insights**: Optimización basada en datos
5. **Business Intelligence**: Métricas de negocio en tiempo real

## 📚 Mejores Prácticas

1. **Usa niveles apropiados**: DEBUG < INFO < WARN < ERROR
2. **Incluye contexto**: IDs de usuario, orden, transacción
3. **Logs estructurados**: Formato consistente y parseable
4. **No loggees información sensible**: Passwords, tokens completos
5. **Considera el rendimiento**: Evita logging excesivo en loops
6. **Usa MDC**: Para contexto automático en threads
7. **Monitorea regularmente**: Revisa métricas y patrones

## 🔧 Configuración Recomendada

### Variables de Entorno
```bash
# Nivel de logging por entorno
LOGGING_LEVEL_COM_LULU=DEBUG          # Desarrollo
LOGGING_LEVEL_COM_LULU=INFO           # Producción

# Configuración de archivos
LOGGING_PATH=/var/log/floreria        # Producción
LOGGING_MAX_HISTORY=90                # Retención en días
```

### JVM Options
```bash
# Para mejor performance de logging
-Dlogback.configurationFile=logback-spring.xml
-XX:+UseG1GC                          # GC optimizado
```

Este sistema de logging proporciona una base sólida para el monitoreo, debugging y análisis de la aplicación de florería virtual.
