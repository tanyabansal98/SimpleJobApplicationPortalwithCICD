# --- Stage 1: Build ---
# Uses a full Maven + Java image to compile your code and produce a JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Run ---
# Uses a lightweight Java-only image (~250MB vs ~800MB)
# No Maven, no source code — just the compiled JAR and the Java runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
