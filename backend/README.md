# Kotlin Spring

This is the main application

---

## Prerequisite

This project `gradle` as the build tool. To run this project, you need the following installed on your system:

- Java Development Kit (JDK) 17
- Gradle
- IntelliJ IDEA (Optional)
- Docker (Optional)

Additionally, this application depends on two service:

- Database (Will crash if it does not exist)
- Sentence Transformer Microservce (Can run without, but will produce error)

---

## Getting Started

1. Build the project
```commandline
./gradlew build
```

---

## Run the application

### Locally

Run the Kotlin Spring using `gradle`:
```commandline
./gradlew bootRun
```
*Note*, the application will need access to the database, edit the configuration file (see below)

### Using Docker (Optional)

1. Build the Docker image
```commandline
docker build -t spring-kotlin-app .
```
*Note*, you may need to edit the `Dockerfile` if the jar name does not match with the built jar.

2. Run the Docker Container:
```commandline
docker run -p 8080:8080 spring-kotlin-app
```
*Note*, the application will need access to the database, edit the configuration file (see below)
The application will start and be available at http://localhost:8080.

---

## Project Structure

```html
src/
├── main/
│   ├── kotlin/
│   │   └── com/chess/puzzle/text2sql/web  # Main Kotlin source code
│   └── resources/                         # Configuration files, static resources, etc.
├── test/
│   └── kotlin/                            # Test code
build.gradle.kts                           # Gradle build file (or pom.xml for Maven)
```

---

## Configuration

- **Application Properties**
Configuration can be found at `src/main/resources/application.properties`

spring.datasource.url -> Database's URL
spring.datasource.username -> Username for Database
spring.datasource.password -> Password for Database
api_key -> API Key for LLM 
base_url -> URL for LLM (DeepSeek is the only one supported, see their docs for their url)
sentence_transformer_url -> Address of the Sentence Transformer Microservice
