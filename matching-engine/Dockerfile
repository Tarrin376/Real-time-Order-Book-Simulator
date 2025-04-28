# First stage: Build with Maven
FROM maven:3.9.9-eclipse-temurin-24-alpine AS builder

WORKDIR /app

# Copy the POM file
COPY ./pom.xml .

# Copy the source code
COPY ./src ./src

# Build the application
RUN mvn clean package

# Second stage: Run with JRE only
FROM eclipse-temurin:24

WORKDIR /app

# Copy the built JAR from the 'builder' stage
COPY --from=builder /app/target/matching-engine-1.0-SNAPSHOT.jar .

# Command to run the matching engine
CMD ["java", "-jar", "matching-engine-1.0-SNAPSHOT.jar"]