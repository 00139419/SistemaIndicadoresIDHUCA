# Usa Tomcat 10 con JDK 17
FROM tomcat:10.1-jdk17-temurin

# Copiar el WAR renombrado como ROOT.war (para despliegue automático)
COPY SistemaRegistrosIDHUCA.war /usr/local/tomcat/webapps-javaee/ROOT.war

# Crear carpeta de uploads (por si se necesita)
RUN mkdir -p /app/uploads

# Exponer el puerto de Tomcat
EXPOSE 8080