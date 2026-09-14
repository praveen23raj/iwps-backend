# Stage 1: Build the JAR file with Maven and Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy dependency definition and source code
COPY pom.xml .
COPY src ./src

# Package the application, skipping tests to speed up the deploy
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image with JRE 17
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/iwps-0.0.1-SNAPSHOT.jar app.jar

# Render assigns a dynamic port via the PORT environment variable
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]