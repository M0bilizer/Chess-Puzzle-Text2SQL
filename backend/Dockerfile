FROM eclipse-temurin:17
WORKDIR /app
COPY build/libs/web-MVP.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]