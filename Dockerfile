# Stage 1: Build Java app
FROM maven:3.9.6-eclipse-temurin-22 AS build

WORKDIR /app

# Copy project source vào container
COPY pom.xml .
COPY src ./src

# Build project, bỏ qua test cho nhanh
RUN mvn clean package -DskipTests

# Stage 2: Run app
FROM eclipse-temurin:22-jdk

WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port app
EXPOSE 8080

# Lệnh chạy app
CMD ["java", "-jar", "app.jar"]
