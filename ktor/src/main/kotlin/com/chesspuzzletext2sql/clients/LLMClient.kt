package com.chesspuzzletext2sql.clients

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Failure
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
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
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
    ): Result<String, Failure> {
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
    ): Result<String, Failure> {
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

    private suspend fun makeRequest(request: ChatCompletionRequest): Result<String, Failure> {
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
                    HttpStatusCode.PaymentRequired -> Err(Error.PaymentRequired)
                    HttpStatusCode.TooManyRequests -> Err(Error.TooManyRequests)
                    HttpStatusCode.InternalServerError -> Err(Error.LLMServerError)
                    HttpStatusCode.ServiceUnavailable -> Err(Error.LLMServiceUnavailable)
                    else ->
                        throw IllegalStateException(
                            "Unexpected HTTP status ${response.status.value} from LLM API"
                        )
                }
            }

            val chatCompletion = response.body<ChatCompletionResponse>()
            Ok(chatCompletion.choices.first().message.content)
        } catch (e: IOException) {
            Err(Error.IOException)
        } catch (e: HttpRequestTimeoutException) {
            Err(Error.TimeoutException)
        }
    }
}
