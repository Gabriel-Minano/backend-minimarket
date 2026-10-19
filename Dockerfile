# ==========================================
# ETAPA 1: CONSTRUIR LA APLICACIÓN
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

# Directorio de trabajo
WORKDIR /app

# Copiar la configuración de Maven
COPY pom.xml .

# Descargar las dependencias
RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src ./src

# Construir el JAR sin ejecutar pruebas
RUN mvn clean package -DskipTests


# ==========================================
# ETAPA 2: EJECUTAR LA APLICACIÓN
# ==========================================
FROM eclipse-temurin:21-jre

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR ejecutable de Spring Boot
COPY --from=builder /app/target/*.jar app.jar

# Puerto configurado en application.properties
EXPOSE 8081

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]