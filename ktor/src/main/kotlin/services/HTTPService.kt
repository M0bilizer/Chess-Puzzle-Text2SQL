package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.model.ChatCompletionRequestBuilder
import com.chesspuzzletext2sql.model.ChatCompletionResponse
import com.chesspuzzletext2sql.model.LLMConfig
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
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
import org.koin.core.component.KoinComponent

val client =
    HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 60000 }
        defaultRequest { contentType(ContentType.Application.Json) }
    }

class HTTPService : KoinComponent {
    suspend inline fun callModel(
        llmConfig: LLMConfig,
        crossinline block: ChatCompletionRequestBuilder.() -> Unit,
    ): Result<String, CustomError> {
        val (provider, modelname, baseUrl, apiKey) = llmConfig
        val body = ChatCompletionRequestBuilder(llmConfig).apply(block).build()

        return try {
            val response =
                client.post(baseUrl) {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    header("Authorization", "Bearer $apiKey")
                    setBody(body)
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
