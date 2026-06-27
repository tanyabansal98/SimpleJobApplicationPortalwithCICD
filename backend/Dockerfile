# --- Stage: Run only ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war", "--spring.profiles.active=docker"]