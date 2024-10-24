import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("io.ktor.plugin") version "2.2.3"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.chess.puzzle.text2sql"
version = "MVP"

application {
	mainClass.set("com.chess.puzzle.text2sql.web.KotlinSpringKt")
}

ktor {
	fatJar {
		archiveFileName.set("fat.jar")
	}
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
	implementation("io.ktor:ktor-client-core:2.3.12")
	implementation("io.ktor:ktor-client-okhttp:2.3.12")
	implementation("com.google.code.gson:gson:2.10.1")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")
	implementation ("com.aallam.openai:openai-client:3.8.2")
	implementation("com.github.jsqlparser:jsqlparser:5.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	implementation("com.lemonappdev:konsist:0.16.1")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks {
	withType<Test> {
		useJUnitPlatform()
	}
	withType<ShadowJar> {
		isZip64 = true
	}
}