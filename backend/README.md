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

2. Run the Docker Container:
```commandline
docker run -p 8080:8080 spring-kotlin-app
```
*Note*, the application will need access to the database, add the environmental variable through docker (see below)
The application will start and be available at http://localhost:8080.

---

## Configuration

- **Application Properties**
  Environment Variable example can be found at `.env-sample`

  If you are using docker, you will have to inject the variable by add the `-e` flag
  For example:
```commandline
docker run -p 8080:8080 -e DB_URL=<my-DB-URL> -e DB_USERNAME=<my-DB-USERNAME> -e ... spring-kotlin-app
```

DB_URL -> Database's URL
DB_USERNAME -> Username for Database
DB_PASSWORD -> Password for Database
DEEPSEEK_API_KEY -> API Key for Deepseek
DEEPSEEK_BASE_URL -> Baseurl for Deepseek
MISTRAL_API_KEY -> API Key for Mistral
MISTRAL_BASE_URL -> Baseurl for Mistral
SENTENCE_TRANSFORMER_URL -> Sentence Transformer Microservice's URL

## Project Structure

```html
src/
├── main/
│   ├── kotlin/
│   │   └── com/chess/puzzle/text2sql/web  # Main Kotlin source code
│   └── resources/                         # Configuration files, static resources, etc.
├── test/
│   └── kotlin/                            # Test code
build.gradle.kts                           # Gradle build file
```

---

## Libraries used

| Gradle Dependency                                        | Description / Purpose             |
|----------------------------------------------------------|-----------------------------------|
| io.ktor:ktor-client-core                                 | Http Client                       |
| io.ktor:ktor-serialization-kotlinx-json                  | Preferred JSON Parsing            |
| com.google.code.gson:gson                                | Alternative JSON Parsing          |
| com.github.jsqlparser:jsqlparser                         | Checking SQL Statement            |
| io.github.oshai:kotlin-logging-jvm                       | Logging                           |
| io.strikt:strikt-core                                    | Assertion                         |
| io.mockk:mockk                                           | Mocking                           |
| com.h2database:h2                                        | In-memory database for test cases |

