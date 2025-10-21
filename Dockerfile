# ====================================================================
# Dockerfile para Backend Spring Boot - Multi-stage Build
# ====================================================================
# Proyecto: Clínica Veterinaria Backend
# Fecha: 2025-01-20
# ====================================================================

# ====================================================================
# STAGE 1: BUILD - Compilar la aplicación con Maven
# ====================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (cache layers)
COPY pom.xml .
COPY .mvn/ .mvn/

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src/ ./src/

# Compilar aplicación (saltar tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Verificar que el JAR se creó correctamente
RUN ls -lh /app/target/*.jar

# ====================================================================
# STAGE 2: RUNTIME - Imagen ligera con solo JRE
# ====================================================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="Clínica Veterinaria"
LABEL description="Backend Spring Boot para gestión de citas veterinarias"
LABEL version="1.0.0"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR compilado desde stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Cambiar permisos
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto de la aplicación
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto (se sobrescriben con docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

