FROM maven:3.9.6-eclipse-temurin-22 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY photos /app/photos

ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

RUN mvn clean package -DskipTests


FROM eclipse-temurin:22-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/photos /app/photos

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
