plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("com.ncorti.ktfmt.gradle") version "0.26.0"
    id("io.kotest") version "6.1.11"
}

group = "com.chesspuzzletext2sql"

version = "0.0.1"

kotlin { compilerOptions { freeCompilerArgs.add("-Xcontext-parameters") } }

application {
    mainClass = "com.chesspuzzletext2sql.ApplicationKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktfmt {
    kotlinLangStyle()
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
    testImplementation("io.kotest:kotest-framework-engine:6.1.11")
    testImplementation("io.kotest:kotest-property:6.1.11")
    testImplementation("io.strikt:strikt-core:0.35.1")
    testImplementation("io.mockk:mockk:1.14.11")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("com.github.jsqlparser:jsqlparser:5.3")
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.3.1")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:2.3.1")
    implementation("com.charleskorn.kaml:kaml-jvm:0.104.0")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
}
