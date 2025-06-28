# Imagen base de Java
FROM openjdk:17-jdk-slim

# Nombre del archivo JAR generado por Maven
ARG JAR_FILE=target/prueba-0.0.1-SNAPSHOT.jar

# Copia el archivo JAR al contenedor
COPY ${JAR_FILE} floreria-app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "floreria-app.jar"]
