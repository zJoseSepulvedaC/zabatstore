# Imagen base con Java 17
FROM openjdk:17-jdk-slim

# Directorio dentro del contenedor
WORKDIR /app

# Copiamos el jar directamente (ya está en la misma carpeta)
COPY zabatstore-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto de la app
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
