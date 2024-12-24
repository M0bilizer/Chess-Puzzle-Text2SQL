FROM eclipse-temurin:17
WORKDIR /app
COPY build/libs/web-1.1.jar ./app.jar

RUN apt-get update && apt-get -y install curl

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]

