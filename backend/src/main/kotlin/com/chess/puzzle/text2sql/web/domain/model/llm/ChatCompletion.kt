package com.chess.puzzle.text2sql.web.domain.model.llm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<Message>,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("n") val n: Int? = null,
    @SerialName("stream") val stream: Boolean? = null,
    @SerialName("stop") val stop: List<String>? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    @SerialName("presence_penalty") val presencePenalty: Double? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("logit_bias") val logitBias: Map<String, Int>? = null,
    @SerialName("user") val user: String? = null,
)

@Serializable
data class Message(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class ChatCompletionResponse(
    @SerialName("id") val id: String,
    @SerialName("object") val `object`: String,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<Choice>,
    @SerialName("usage") val usage: Usage,
)

@Serializable
data class Choice(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: Message,
    @SerialName("finish_reason") val finishReason: String,
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
)
