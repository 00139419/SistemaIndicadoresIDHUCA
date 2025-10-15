# Usa la imagen base de Nginx
FROM nginx:alpine

# Define la raíz de contexto como variable (por si se quiere cambiar fácilmente)
ARG CONTEXT_PATH=/sistemaRegistrosIdhuca

# Copia la compilación del frontend al directorio de Nginx
COPY build/ /usr/share/nginx/html/

# Copia la configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer el puerto HTTP
EXPOSE 80

# Inicia Nginx en primer plano
CMD ["nginx", "-g", "daemon off;"]
