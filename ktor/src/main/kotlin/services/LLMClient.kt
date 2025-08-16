package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.model.AvailablePromptTemplate.name
import com.chesspuzzletext2sql.model.ChatCompletionRequest
import com.chesspuzzletext2sql.model.ChatCompletionResponse
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.Message
import com.chesspuzzletext2sql.model.PromptTemplate
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

val client =
    HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 60000 }
        defaultRequest { contentType(ContentType.Application.Json) }
    }

class LLMClient(private val config: LLMConfig) {
    suspend fun call(
        messages: List<Message>,
        stream: Boolean = false,
        temperature: Double? = null,
        maxTokens: Int? = null,
    ): Result<String, CustomError> {
        require(messages.isNotEmpty()) { "At least one message is required" }
        require(messages.any { it.role == "user" }) { "At least one user message is required" }
        val lastUserMessage = messages.last { it.role == "user" }.content
        logger.info {
            "Calling model (provider=${config.provider}, model=${config.modelName}) with Message(lastUserMessage = $lastUserMessage)"
        }
        return makeRequest(
            ChatCompletionRequest(
                model = config.modelName,
                messages = messages,
                stream = stream,
                temperature = temperature,
                maxTokens = maxTokens,
            )
        )
    }

    suspend fun call(
        template: PromptTemplate,
        userInput: String,
        stream: Boolean = false,
        temperature: Double? = null,
        maxTokens: Int? = null,
    ): Result<String, CustomError> {
        println("ok")
        logger.info {
            "Calling model (provider=${config.provider}, model=${config.modelName}) " +
                "with PromptTemplate(name = ${template.name}, input = $userInput)"
        }
        return makeRequest(
            ChatCompletionRequest(
                model = config.modelName,
                messages = listOf(Message("user", template(userInput))),
                stream = stream,
                temperature = temperature,
                maxTokens = maxTokens,
            )
        )
    }

    private suspend fun makeRequest(request: ChatCompletionRequest): Result<String, CustomError> {
        return try {
            val response =
                client.post(config.baseUrl) {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    header("Authorization", "Bearer ${config.apiKey}")
                    setBody(request)
                }

            if (!response.status.isSuccess()) {
                when (response.status) {
                    HttpStatusCode.PaymentRequired -> Err(SystemError.PaymentRequired)
                    HttpStatusCode.TooManyRequests -> Err(SystemError.TooManyRequests)
                    HttpStatusCode.InternalServerError -> Err(SystemError.LLMServerError)
                    HttpStatusCode.ServiceUnavailable -> Err(SystemError.LLMServiceUnavailable)
                    else ->
                        throw IllegalStateException(
                            "Unexpected HTTP status ${response.status.value} from LLM API"
                        )
                }
            }

            val chatCompletion = response.body<ChatCompletionResponse>()
            Ok(chatCompletion.choices.first().message.content)
        } catch (e: IOException) {
            Err(SystemError.IOException)
        } catch (e: HttpRequestTimeoutException) {
            Err(SystemError.TimeoutException)
        }
    }
}
