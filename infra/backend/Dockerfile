# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar pom.xml primeiro para cache de dependências
COPY backend/pom.xml .
RUN mvn -B -ntp -q dependency:go-offline

# Copiar código fonte e buildar
COPY backend/src ./src
RUN mvn -B -ntp -q clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar curl para o healthcheck do docker-compose
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
