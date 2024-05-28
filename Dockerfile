# Use an official Maven image with JDK 17 to build the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Use an official OpenJDK image with JDK 17 to run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the packaged jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
