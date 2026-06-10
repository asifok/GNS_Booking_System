# -------- Build Stage --------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy everything
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# -------- Run Stage --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (just for reference)
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
