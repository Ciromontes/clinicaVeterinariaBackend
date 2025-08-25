
---

# 🐾 Backend Gestión de Citas – Spring Boot con Frontend Integrado
**Evidencia GA7-220501096-AA4-EV03 – Componente Backend**

Aplicación desarrollada con **Spring Boot**, empaquetada en un archivo `.jar` que **sirve el frontend (React/Vite)** desde recursos estáticos y expone **APIs REST** para autenticación, gestión de citas y mascotas.

---

## 🌐 Accesos locales

- **Frontend:** [http://localhost:8080](http://localhost:8080)
- **APIs REST:**
    - `POST /api/auth/login`
    - `GET /api/citas`, `GET /api/citas/{id}`, `POST`, `PUT`, `DELETE /api/citas/{id}`
    - `GET /api/mascotas`, `GET /api/mascotas/{id}`, `POST /api/mascotas`

---

## ⚙️ Requisitos técnicos

- Java 17 o superior
- Maven 3.9+
- MySQL en ejecución
- Puerto 8080 libre (o configurar `server.port`)

---

## 🛠️ Configuración del proyecto

Editar el archivo `src/main/resources/application.properties` con los datos de conexión y secreto JWT:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinicaveterinaria
spring.datasource.username=SU_USUARIO
spring.datasource.password=SU_PASSWORD
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
server.port=8080
jwt.secret=una_clave_secreta_segura
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

> Asegúrese de que el esquema y las tablas estén creadas en MySQL.

---

## 🔗 Integración del frontend (React/Vite)

### 1. Construir el frontend

Desde la carpeta del frontend:

```bash
npm ci
npm run build
```

### 2. Copiar archivos estáticos al backend

```powershell
New-Item -ItemType Directory -Force -Path .\src\main\resources\static
Remove-Item .\src\main\resources\static\* -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item .\ruta\al\frontend\dist\* .\src\main\resources\static\ -Recurse -Force
```

> Resultado esperado:
- `static/index.html`
- `static/assets/...`

---

## 📦 Construcción del JAR

Desde la raíz del backend:

```bash
mvn clean package -DskipTests
```

> El archivo generado estará en `target/gestion-citas-0.0.1-SNAPSHOT.jar`

---

## 🚀 Ejecución del backend

```bash
java -jar .\target\gestion-citas-0.0.1-SNAPSHOT.jar
```

- Frontend: [http://localhost:8080](http://localhost:8080)
- APIs: [http://localhost:8080/api/...](http://localhost:8080/api/...)

### Cambiar el puerto (opcional)

```bash
java -jar .\target\gestion-citas-0.0.1-SNAPSHOT.jar --server.port=8081
```

---

## 📡 Endpoints principales

| Módulo         | Método | Endpoint                     |
|----------------|--------|------------------------------|
| Autenticación  | POST   | `/api/auth/login`            |
| Citas          | GET    | `/api/citas`, `/api/citas/{id}`  
|                | POST   | `/api/citas`  
|                | PUT    | `/api/citas/{id}`  
|                | DELETE | `/api/citas/{id}`  
| Mascotas       | GET    | `/api/mascotas`, `/api/mascotas/{id}`  
|                | POST   | `/api/mascotas`  

---

## 🧪 Ejemplo rápido de login con `curl`

```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"usuario@example.com\", \"password\":\"tu_clave\"}"
```

---
