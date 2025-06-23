FROM openjdk:22-jdk

WORKDIR /app

COPY target/JobPortal-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java","-jar","app.jar"]