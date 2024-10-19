package com.chess.puzzle.text2sql.web.service

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.chess.puzzle.text2sql.web.helper.PropertyHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LargeLanguageApiService (
    @Autowired private val propertyHelper: PropertyHelper
) {
    private val apiKey = propertyHelper.apiKey
    private val baseUrl = propertyHelper.baseUrl
    private val client: OpenAI = OpenAI(token = apiKey, host = OpenAIHost(baseUrl))

    suspend fun callDeepSeek() {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("deepseek-chat"),
            messages = listOf(
                ChatMessage(role = ChatRole.System, content = "You are a helpful assistant"),
                ChatMessage(role = ChatRole.User, content = "Hello")
            ),
            temperature = 0.0
        )
        val response: ChatCompletion = client.chatCompletion(chatCompletionRequest)
        response.choices.forEach { (chatCompletion) -> println(chatCompletion) }
    }

}