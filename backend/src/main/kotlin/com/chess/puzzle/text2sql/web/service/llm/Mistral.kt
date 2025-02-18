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
class Mistral(@Qualifier("mistralClient") private val client: OpenAI) : LargeLanguageModel {
    override suspend fun callModel(query: String): ChatCompletion {
        val chatCompletionRequest =
            ChatCompletionRequest(
                model = ModelId("mistral-small-latest"),
                messages = listOf(ChatMessage(role = ChatRole.User, content = query)),
            )
        return client.chatCompletion(chatCompletionRequest)
    }
}
