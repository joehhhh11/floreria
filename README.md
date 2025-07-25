# 🌸 Florería Virtual - Sistema de E-commerce

![Diseño Principal](docs/design.png)
![Diseño Secundario](docs/design2.png)

## 📋 Descripción

Una plataforma de e-commerce moderna especializada en la venta de flores para ocasiones especiales. El sistema permite a los usuarios explorar un catálogo diverso de productos florales, realizar compras online y gestionar pedidos de manera eficiente.

## ✨ Características Principales

- **🌺 Catálogo de Productos**: Amplia variedad de flores organizadas por categorías
- **🛒 Carrito de Compras**: Sistema completo de gestión de pedidos
- **💳 Pasarela de Pago**: Integración con Stripe para pagos seguros
- **🔐 Autenticación**: Sistema de usuarios con JWT y 2FA
- **📊 Panel de Administración**: Gestión completa de productos e inventario
- **📁 Importación Masiva**: Carga de productos mediante archivos Excel
- **🔍 Filtros Avanzados**: Búsqueda por categoría, precio y disponibilidad
- **📱 Responsive Design**: Interfaz adaptable a dispositivos móviles

## 🏗️ Arquitectura del Sistema

### Diagramas
![Diagrama de Clases](docs/diagramaclases.png)
![Diagrama Entidad-Relación](docs/entidadrelacionn.png)

### Arquitectura Técnica
- **Patrón**: MVC (Model-View-Controller)
- **Arquitectura**: Microservicios con Spring Boot
- **Seguridad**: JWT + Spring Security
- **Base de Datos**: JPA + Hibernate con MySQL/H2
## 📘 Casos de Uso

### 📌 Registrar Cliente Nuevo
- **Actores**: Cliente  
- **Descripción**: El cliente podrá crear una cuenta proporcionando sus datos personales.  
- **Precondición**: El cliente no debe tener una cuenta registrada.  
- **Postcondición**: Se crea una cuenta de usuario y se notifica al cliente.

**Flujos Básicos**:
1. El cliente accede a la opción de “Registrarse”.
2. El cliente ingresa sus datos.
3. El sistema valida la información.
4. Se envía un código de verificación al correo electrónico.
5. El sistema guarda los datos y crea la cuenta.

**Flujos Alternativos**:
- Si el cliente ingresa un correo ya registrado, el sistema muestra un mensaje de error.

### 🔐 Iniciar Sesión
- **Actores**: Cliente  
- **Descripción**: Permite al cliente ingresar a su cuenta para gestionar pedidos.  
- **Precondición**: El cliente debe estar registrado.  
- **Postcondición**: El cliente accede a su panel personal.

**Flujos Básicos**:
1. Accede al formulario de inicio de sesión.
2. Ingresa correo y contraseña.
3. El sistema valida las credenciales.
4. Accede a su cuenta.

**Flujos Alternativos**:
- Si ingresa datos incorrectos, se muestra un mensaje de error.

### 🛍️ Registrar Pedido
- **Actores**: Cliente  
- **Descripción**: El cliente selecciona productos y realiza el pago.  
- **Precondición**: Debe estar registrado e iniciar sesión.  
- **Postcondición**: Pedido registrado y se envía confirmación.

**Flujos Básicos**:
1. Selecciona productos y los añade al carrito.
2. Revisa el carrito y procede a la compra.
3. Proporciona datos de envío y método de pago.
4. Se confirma el pedido y se envía un recibo.

**Flujos Alternativos**:
- Si no hay saldo, se solicita otro método de pago.
- Si el producto no está disponible, se notifica.

### 🛠️ Gestionar Productos
- **Actores**: Administrador  
- **Descripción**: Agregar, editar y eliminar productos desde el panel.  
- **Precondición**: Debe iniciar sesión como administrador.  
- **Postcondición**: Catálogo actualizado.

**Flujos Básicos**:
1. Accede al panel de administración.
2. Elige agregar producto e ingresa datos.
3. Guarda la información.

**Flujos Alternativos**:
- Error al subir datos, se muestra mensaje.

### 📈 Generar Reporte de Ventas
- **Actores**: Administrador de ventas  
- **Descripción**: Generar reportes de ventas por fechas.  
- **Precondición**: Estar autenticado y tener acceso a reportes.  
- **Postcondición**: Reportes generados y disponibles.

**Flujos Básicos**:
1. Accede a sección de reportes.
2. Selecciona fechas y filtros.
3. Se genera y visualiza el reporte.

**Flujos Alternativos**:
- Si no hay datos, se notifica.
- Se pueden aplicar más filtros

## 🛠️ Tecnologías Utilizadas

### Backend
- **Framework**: Spring Boot 3.4.5
- **Lenguaje**: Java 17
- **Módulos Spring**:
  - Spring MVC (API REST)
  - Spring Data JPA (Persistencia)
  - Spring Security (Autenticación/Autorización)
  - Spring Validation (Validaciones)

### Base de Datos
**Producción**: PostgreSQL (Railway) / MySQL (Local)
**Desarrollo**: MySQL
**Pruebas**: H2 en memoria

### Herramientas de Desarrollo
- **Build Tool**: Maven
- **Documentación API**: OpenAPI/Swagger
- **Pasarela de Pago**: Stripe API
- **Autenticación**: JWT (JSON Web Tokens)
- **Logging**: Logback + SLF4J

### Testing
- **Framework**: JUnit 5
- **Mocking**: Mockito
- **Integración**: Spring Boot Test
- **Cobertura**: 90+ pruebas (unitarias + integración)

## 📋 Requisitos del Sistema

- **Java**: JDK 17 o superior
- **Maven**: 3.6 o superior
- **Base de Datos**: PostgreSQL (Railway) / MySQL 8.0+ (local)
- **RAM**: Mínimo 512MB, recomendado 1GB
- **Espacio**: 100MB para la aplicación + espacio para BD

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/joehhhh11/floreria.git
cd floreria
```

### 2. Configurar Base de Datos

#### Para Desarrollo (MySQL)
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/prueba2db
spring.datasource.username=root
spring.datasource.password=Joehxd123!
spring.jpa.hibernate.ddl-auto=update
```

#### Para Pruebas (H2)
```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```
#### TEST
#### Para Producción (PostgreSQL - Railway)
```properties
# application-prod.properties
*Visualización de pruebas automatizadas ejecutadas sobre el sistema.*
spring.datasource.url=jdbc:postgresql://postgres.railway.internal:5432/railway
spring.datasource.username=postgres
spring.datasource.password=${PGPASSWORD:defaultpassword}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

### 3. Variables de Entorno
JWT_SECRET=tu_clave_secreta_jwt_muy_larga_y_segura
# Stripe Configuration
STRIPE_SECRET_KEY=sk_test_tu_clave_secreta_stripe
STRIPE_PUBLISHABLE_KEY=pk_test_tu_clave_publica_stripe

# Database (Producción)
DB_URL=jdbc:mysql://localhost:3306/floreria_db
DB_USERNAME=usuario
DB_PASSWORD=contraseña
```




#### Desarrollo
```bash

#### Producción
```bash
./mvnw clean package
java -jar target/floreria-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```


- **usuarios**: Gestión de usuarios y autenticación
- **productos**: Catálogo de flores y productos
- **categoria**: Clasificación de productos
- **orders**: Gestión de pedidos
### Script de Inicialización
📂 [Ver script completo](docs/floreria_db.sql)

## 🧪 Sistema de Pruebas

![Pruebas Automatizadas](docs/test.jpg)

### Ejecutar Todas las Pruebas
./mvnw test

### Tipos de Pruebas Implementadas

#### ✅ Pruebas Unitarias (7 módulos)
- **ProductControllerTest**: Controladores REST
- **AuthServiceTest**: Autenticación y autorización
- **OrderServiceTest**: Gestión de pedidos

#### ✅ Pruebas de Integración (3 categorías)
- **Repository Tests**: Persistencia de datos (10 tests)

- **Total de Pruebas**: 7+ clases de prueba
- **Cobertura**: Unitarias + Integración básica
- **Autenticación Mock**: Configurada para pruebas
- **Base de Datos**: MySQL para pruebas
```bash
# Pruebas de productos
./mvnw test -Dtest="*Product*Test*"

# Pruebas de autenticación
./mvnw test -Dtest="*Auth*Test*"
# Pruebas de pedidos
```

## 📡 API Documentation

- **Local**: http://localhost:8080/swagger-ui.html
- **Documentación JSON**: http://localhost:8080/v3/api-docs

### Endpoints Principales

POST /api/auth/login        - Iniciar sesión
POST /api/auth/register     - Registrar usuario
POST /api/auth/register/clerk - Registrar usuario con Clerk
POST /api/auth/clerk-login  - Iniciar sesión con Clerk
```

#### 🌸 Productos
```
GET    /api/products             - Listar productos
GET    /api/products/{id}        - Obtener producto
POST   /api/products             - Crear producto (admin)
PUT    /api/products/{id}        - Actualizar producto (admin)
DELETE /api/products/{id}        - Eliminar producto (admin)
POST   /api/products/import      - Importar desde Excel (admin)
```

#### 🛒 Pedidos
```
GET    /api/orders              - Mis pedidos
POST   /api/orders              - Crear pedido
GET    /api/orders/{id}         - Detalle de pedido
```

## 🔧 Configuración Avanzada

### Perfiles de Aplicación
- **dev**: Desarrollo con H2 y logs detallados
- **test**: Pruebas automatizadas
- **prod**: Producción con MySQL y logs optimizados

### Configuración de Seguridad
```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 horas
  
security:
  enabled: true
  cors:
    allowed-origins: ["http://localhost:3000", "https://tu-frontend.com"]
```

## 📝 Logs y Monitoreo

### Configuración de Logs
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
    </appender>
</configuration>
```

### Endpoints de Monitoreo
```
GET /actuator/health     - Estado de la aplicación
GET /actuator/metrics    - Métricas de performance
GET /actuator/info       - Información de la aplicación
```

## 🤝 Contribuir

### Proceso de Desarrollo
1. Fork el repositorio
2. Crea una rama feature: `git checkout -b feature/nueva-funcionalidad`
3. Realiza tus cambios
4. Ejecuta las pruebas: `./mvnw test`
5. Commit: `git commit -m "feat: descripción del cambio"`
6. Push: `git push origin feature/nueva-funcionalidad`
7. Abre un Pull Request

### Estándares de Código
- **Formato**: Google Java Style Guide
- **Testing**: Cobertura mínima del 80%
- **Documentación**: JavaDoc para métodos públicos
- **Commits**: Conventional Commits

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Equipo de Desarrollo

- **Backend Developer**: [joehhhh11](https://github.com/joehhhh11)
- **Proyecto**: Florería Virtual E-commerce

## 📞 Soporte

- **Issues**: [GitHub Issues](https://github.com/joehhhh11/floreria/issues)
- **Documentación**: [Wiki del Proyecto](https://github.com/joehhhh11/floreria/wiki)

---

⭐ Si te ha gustado este proyecto, ¡no olvides darle una estrella!

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue)
![Tests](https://img.shields.io/badge/Tests-90+-brightgreen)
![License](https://img.shields.io/badge/License-MIT-yellow)
