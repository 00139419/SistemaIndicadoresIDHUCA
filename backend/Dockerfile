#Usa Tomcat 10 con JDK 17
FROM tomcat:10.1-jdk17-temurin

#Instalar dependencias del sistema
RUN apt-get update && \
    apt-get install -y \
    postgresql-client \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

#Verificar instalación de pg_dump
RUN pg_dump --version

#Copiar el WAR renombrado como ROOT.war (para despliegue automático)
COPY SistemaRegistrosIDHUCA.war /usr/local/tomcat/webapps-javaee/ROOT.war

#Crear carpeta de uploads (por si se necesita)
RUN mkdir -p /app/uploads /app/data/database/backups /app/data/logs

#Exponer el puerto de Tomcat
EXPOSE 8080