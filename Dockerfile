FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar Maven Wrapper (importante para que funcione)
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml .

# Dar permisos de ejecución
RUN chmod +x mvnw

# Descargar dependencias (cache)
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Copiar código fuente
COPY src ./src

# Compilar
RUN ./mvnw clean package -DskipTests --no-transfer-progress


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
