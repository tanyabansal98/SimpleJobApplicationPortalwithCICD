# --- Stage: Run only ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/job-application-portal-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war", "--spring.profiles.active=docker"]