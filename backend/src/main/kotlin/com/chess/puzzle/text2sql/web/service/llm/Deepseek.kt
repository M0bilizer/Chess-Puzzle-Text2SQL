package com.chess.puzzle.text2sql.web.service.llm

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class Deepseek(@Qualifier("deepSeekClient") private val client: OpenAI) : LargeLanguageModel {
    override suspend fun callModel(query: String): ChatCompletion {
        val chatCompletionRequest =
            ChatCompletionRequest(
                model = ModelId("deepseek-chat"),
                messages =
                    listOf(
                        ChatMessage(
                            role = ChatRole.System,
                            content = "You are a helpful assistant",
                        ),
                        ChatMessage(role = ChatRole.User, content = query),
                    ),
                temperature = 0.0,
            )
        return client.chatCompletion(chatCompletionRequest)
    }
}
