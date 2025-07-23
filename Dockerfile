# Multi-stage Dockerfile para Railway - Versión simplificada
FROM openjdk:21-jdk-slim AS build

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (cache layer)
RUN mvn dependency:go-offline -B || true

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests -B

# Etapa final - imagen ligera
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/floreria-0.0.1-SNAPSHOT.jar app.jar

# Copiar script de inicio
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Exponer puerto
EXPOSE 8080

# Variables de entorno para Railway
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=${PORT:-8080}

# Ejecutar aplicación usando script de inicio
ENTRYPOINT ["/start.sh"]
