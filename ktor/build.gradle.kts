plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ktor)
  alias(libs.plugins.kotlin.plugin.serialization)
  id("com.ncorti.ktfmt.gradle") version "0.22.0"
  id("io.kotest") version "6.0.0"
  id("com.google.devtools.ksp") version "2.2.20-2.0.3"
}

group = "com.chesspuzzletext2sql"

version = "0.0.1"

application {
  mainClass = "com.chesspuzzletext2sql.ApplicationKt"

  val isDevelopment: Boolean = project.ext.has("development")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktfmt {
  googleStyle()
  removeUnusedImports.set(true)
}

repositories { mavenCentral() }

dependencies {
  implementation(libs.koin.ktor)
  implementation(libs.koin.logger.slf4j)
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.exposed.core)
  implementation(libs.exposed.jdbc)
  implementation(libs.h2)
  implementation(libs.ktor.server.call.logging)
  implementation(libs.ktor.server.request.validation)
  implementation(libs.ktor.server.cors)
  implementation(libs.ktor.server.cio)
  testImplementation(libs.ktor.server.test.host)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.cio)
  implementation(libs.ktor.client.content.negotiation)
  implementation(libs.logback.classic)
  testImplementation("io.kotest:kotest-framework-engine:6.0.0")
  testImplementation("io.kotest:kotest-property:6.0.0")
  testImplementation("io.strikt:strikt-core:0.34.0")
  testImplementation("io.mockk:mockk:1.14.5")
  implementation("mysql:mysql-connector-java:8.0.33")
  implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
  implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
  implementation("com.github.jsqlparser:jsqlparser:5.1")
  implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.1")
  implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:2.0.1")
  implementation("com.charleskorn.kaml:kaml-jvm:0.85.0")
  implementation("dev.nesk.akkurate:akkurate-core:0.11.0")
  ksp("dev.nesk.akkurate:akkurate-ksp-plugin:0.11.0")
}
