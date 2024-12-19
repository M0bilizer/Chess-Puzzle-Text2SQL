package com.chess.puzzle.text2sql.web.config

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpClientConfig {

    @Value("\${api_key}") lateinit var apiKey: String

    @Value("\${base_url}") lateinit var baseUrl: String

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(OkHttp) { install(ContentNegotiation) { json() } }
    }

    @Bean
    fun openAi(): OpenAI {
        val apiKey = apiKey
        val baseUrl = baseUrl
        val loggingConfig =
            LoggingConfig(logLevel = LogLevel.None, logger = Logger.Simple, sanitize = true)
        return OpenAI(token = apiKey, host = OpenAIHost(baseUrl), logging = loggingConfig)
    }
}
