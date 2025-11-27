# Use Java 21 runtime as base image
FROM eclipse-temurin:21-jre-alpine

# Set working directory inside container
WORKDIR /app

# Copy the JAR file from build/libs to container
COPY build/libs/*.jar todo-0.0.1-SNAPSHOT.jar

# Expose the port your app runs on
EXPOSE 8081

# Command to run your application
ENTRYPOINT ["java", "-jar", "todo-0.0.1-SNAPSHOT.jar"]