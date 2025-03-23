# 🌎 Sistema de Indicadores para IDHUCA  

## 📌 Descripción  
El **Sistema de Indicadores para IDHUCA** es una plataforma diseñada para centralizar información de diversas fuentes y generar gráficos e infografías, facilitando el análisis de datos en la organización.  

## ⚙️ Tecnologías Utilizadas  
### **Backend**  
- **Spring Boot** (Framework de desarrollo)  
- **PostgreSQL** (Base de datos)  
- **JPA/Hibernate** (Manejo de datos)  
- **Spring Security** (Autenticación y autorización)  
- **JWT** (Manejo de tokens)  

### **Frontend**  
- **React** (Interfaz de usuario)  

### **Infraestructura**  
- **Apache/WildFly** (Servidor de aplicaciones)  
- **Starlink o cualquier red** (Red)  


## 📂 Estructura del Proyecto  
```
📦 backend (API en Spring Boot)
 ├── src/main/java/com/sv/uca/idhuca/indicadores
 │   ├── config/         # Configuración del sistema
 │   ├── controllers/    # Controladores REST
 │   ├── dto/            # Data Transfer Objects
 │   ├── entities/       # Entidades JPA
 │   ├── repositories/   # Repositorios de datos
 │   ├── services/       # Lógica de negocio
 │   ├── security/       # Seguridad y autenticación
 │   ├── utils/          # Utilidades generales
 ├── src/main/resources/
 │   ├── application.properties  # Configuración de la aplicación
 │   ├── schema.sql  # Scripts de creación de base de datos
 │   ├── data.sql    # Datos iniciales de prueba
```

## 📜 Módulos del Sistema  
- **Registros**: Ingreso y gestión de datos.  
- **Segunda Validación**: Control de calidad de la información.  
- **Usuarios (Admin)**: Gestión de accesos y roles.  
- **Mantenimiento (Admin)**: Catálogos y configuraciones generales.  
- **Configuraciones (Admin)**: Parametrización del sistema.  
- **Auditoría (Admin)**: Registro de acciones dentro del sistema.  

## 🚀 Instalación y Ejecución  

### **1️⃣ Clonar el Repositorio**  
```bash
git clone https://github.com/00139419/SistemaIndicadoresIDHUCA.git
cd sistema-indicadores
```

### **2️⃣ Configurar el Backend**  
#### 📌 Crear el archivo `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/idhuca_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.security.jwt.secret=tu_secreto_jwt
```

### **3️⃣ Ejecutar el Backend**  
```bash
cd backend
mvn spring-boot:run
```

### **4️⃣ Configurar y Ejecutar el Frontend**  
```bash
cd frontend
npm install
npm run dev
```

## 📖 Licencia  
Este proyecto está bajo la licencia [MIT](LICENSE).  
