# Stage 1: Build

FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# stage 2: runtime stage

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build app/targert/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java","-jar","app.jar"]