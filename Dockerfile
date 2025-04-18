# Step 1: Builder stage
# Using an image of Java 17 and Maven
FROM maven:3.8.4-openjdk-17-slim AS builder
# Specifying the working directory for the build
WORKDIR /app
# Copying the configuration files
COPY pom.xml .
# Loading dependencies (this is cached if pom.xml does not change)
RUN mvn dependency:go-offline
# Copying the source code
COPY src ./src
# Building a project
RUN mvn clean package -DskipTests
# A new container for launching the application
FROM openjdk:17-jdk-slim

# Step 2: Production stage
# Specifying the working directory
WORKDIR /app
# Копируем сгенерированный .jar из предыдущего контейнера
COPY --from=builder /app/target/taskmanagement-0.0.1-SNAPSHOT.jar app.jar
# Запускаем приложение
CMD ["java", "-jar", "app.jar"]