# Stage 1: Build the application
FROM gradle:8.10.1-jdk17 AS build

WORKDIR /app
COPY . .
RUN gradle build

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/build/libs/web-1.jar ./app.jar

RUN apt-get update && apt-get -y install curl


EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
