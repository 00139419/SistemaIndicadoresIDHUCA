FROM openjdk:17-jdk-slim

WORKDIR /app

# Crear directorios necesarios
RUN mkdir -p /app/data/backups \
    && mkdir -p /app/data/uploads \
    && mkdir -p /app/data/logs \
    && chmod -R 777 /app/data

COPY target/*.jar application.jar

EXPOSE 8080

CMD ["java", "-jar", "application.jar"]