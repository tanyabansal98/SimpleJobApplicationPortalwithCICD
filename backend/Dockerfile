# --- Stage 1: Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime Stage ---
FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app

# Copy everything needed for mvn spring-boot:run
COPY --from=build /app/src ./src
COPY --from=build /app/pom.xml ./pom.xml
COPY --from=build /root/.m2 /root/.m2 

EXPOSE 8080

# Run using Maven plugin to ensure JSP support
# We add -Dspring-boot.run.fork=false to prevent classloader issues in Docker
ENTRYPOINT ["mvn", "spring-boot:run", "-Dspring-boot.run.profiles=docker", "-Dspring-boot.run.fork=false"]
