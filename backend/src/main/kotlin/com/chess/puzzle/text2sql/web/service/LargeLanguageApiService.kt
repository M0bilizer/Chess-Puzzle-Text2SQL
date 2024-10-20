package com.chess.puzzle.text2sql.web.service

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.chess.puzzle.text2sql.web.helper.PropertyHelper
import com.chess.puzzle.text2sql.web.helper.ResultWrapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LargeLanguageApiService (
    @Autowired private val propertyHelper: PropertyHelper
) {
    private val apiKey = propertyHelper.apiKey
    private val baseUrl = propertyHelper.baseUrl
    private val client: OpenAI = OpenAI(token = apiKey, host = OpenAIHost(baseUrl))

    suspend fun callDeepSeek(input: String): ResultWrapper {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("deepseek-chat"),
            messages = listOf(
                ChatMessage(role = ChatRole.System, content = "You are a helpful assistant"),
                ChatMessage(role = ChatRole.User, content = input)
            ),
            temperature = 0.0
        )
        val chatCompletion = client.chatCompletion(chatCompletionRequest)
        val response = chatCompletion.choices.firstOrNull()?.message?.messageContent
        logger.info { "Calling DeepSeek { input = $input } -> { response = $response }" }
        return when (val textContent: Content? = chatCompletion.choices.firstOrNull()?.message?.messageContent) {
            is TextContent -> ResultWrapper.Sucesss(textContent.content)
            else -> {
                logger.warn { "Calling DeepSeek { input = $input } -> { Received image and texts }"}
                ResultWrapper.ResponseError
            }
        }
    }
}