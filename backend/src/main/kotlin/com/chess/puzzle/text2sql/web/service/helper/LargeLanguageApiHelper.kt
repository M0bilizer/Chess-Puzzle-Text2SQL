package com.chess.puzzle.text2sql.web.service.helper

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.Content
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.chess.puzzle.text2sql.web.entities.helper.Property
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LargeLanguageApiHelper(
    @Autowired private val propertyHelper: Property,
) {
    private val apiKey = propertyHelper.apiKey
    private val baseUrl = propertyHelper.baseUrl
    private val client: OpenAI = OpenAI(token = apiKey, host = OpenAIHost(baseUrl))

    suspend fun callDeepSeek(input: String): ResultWrapper<out String> {
        val chatCompletionRequest =
            ChatCompletionRequest(
                model = ModelId("deepseek-chat"),
                messages =
                    listOf(
                        ChatMessage(role = ChatRole.System, content = "You are a helpful assistant"),
                        ChatMessage(role = ChatRole.User, content = input),
                    ),
                temperature = 0.0,
            )
        val chatCompletion = client.chatCompletion(chatCompletionRequest)
        val response: Content? = chatCompletion.choices.firstOrNull()?.message?.messageContent
        logger.info { "Calling DeepSeek { input = $input } -> { response = $response }" }
        return when (response) {
            is TextContent -> ResultWrapper.Success(response.content)
            else -> {
                logger.warn { "Calling DeepSeek { input = $input } -> { Received image and texts }" }
                ResultWrapper.Error.ResponseError
            }
        }
    }
}
