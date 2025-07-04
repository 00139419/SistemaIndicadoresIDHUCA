# Imagen de nginx
FROM nginx:alpine

# Copia tu build al directorio de nginx
COPY build/ /usr/share/nginx/html

# Reemplaza la config default de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer el puerto
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
