# Stage 1: Build the application
FROM gradle:9.6.1-jdk25 AS build

WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:25-alpine
WORKDIR /app
COPY --from=build /app/build/libs/ktor-all.jar ./ktor-all.jar
COPY --from=build /app/prompt-templates.yaml ./prompt-templates.yaml

RUN apk add --no-cache curl


EXPOSE 8080
CMD ["java", "-jar", "ktor-all.jar"]