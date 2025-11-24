==========================================
Proyecto: Zabat Store
==========================================
Autores: José Sepúlveda Concha
Asignatura: Seguridad y Calidad en el Desarrollo de Software
Universidad: [Nombre de la institución]
Fecha: [27/10/2025]

## Descripción:

Aplicación web desarrollada con Spring Boot, Spring Security y Thymeleaf
que permite el arriendo de maquinaria agrícola entre usuarios.

## Componentes:

- Backend y frontend integrados en Spring Boot
- Seguridad con autenticación y autorización (Spring Security)
- Registro y login de usuarios con contraseñas encriptadas
- Perfil de usuario con datos privados
- Páginas públicas: home, registro, login, maquinarias
- Páginas privadas: perfil, publicación y reserva de maquinarias

## Requisitos:

- Java 17 o superior
- Maven 3.x
- Navegador web moderno

## Ejecución:

1. Abrir consola en la carpeta del proyecto
2. Ejecutar: ./mvnw spring-boot:run
3. Abrir en el navegador: http://localhost:8080/home

==========================================
Desarrollado con fines académicos
==========================================

EJECUTAR CON VM

cd ~/zabatstore

rm -f src/main/java/com/zabatstore/zabatstore/config/PasswordConfig.java

docker rm -f zabatstore 2>/dev/null
docker build --no-cache -t zabatstore:latest .
docker run -d --name zabatstore -p 80:8080 zabatstore:latest
docker logs -f zabatstore


mvn clean package -DskipTests
