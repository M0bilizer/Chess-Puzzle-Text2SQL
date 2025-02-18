import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("io.ktor.plugin") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.ncorti.ktfmt.gradle") version "0.21.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "com.chess.puzzle.text2sql"
version = "1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.chess.puzzle.text2sql.web.KotlinSpringKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    isZip64 = true
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

ktfmt {
    // KotlinLang style - 4 space indentation - From kotlinlang.org/docs/coding-conventions.html
    kotlinLangStyle()
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("Kotlin Spring")
        }
    }
}

configurations.matching { it.name.startsWith("dokka") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group.startsWith("com.fasterxml.jackson")) {
            useVersion("2.15.3")
        }
    }
}

//tasks.test {
//    enabled = false
//}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("io.ktor:ktor-client-core:2.3.12") // MUST BE 2.3.12
    implementation("io.ktor:ktor-client-okhttp:2.3.12") // MUST BE 2.3.12
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.aallam.openai:openai-client:3.8.2")
    implementation("com.github.jsqlparser:jsqlparser:5.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("io.strikt:strikt-spring:0.34.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("com.h2database:h2:2.3.232")
    testImplementation("io.ktor:ktor-client-mock:2.3.12") // MUST BE 2.3.12
}
