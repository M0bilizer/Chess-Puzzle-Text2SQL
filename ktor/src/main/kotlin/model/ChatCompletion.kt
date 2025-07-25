package com.chesspuzzletext2sql.model

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

class ChatCompletionRequestBuilder(private val llmConfig: LLMConfig) {
    var messages: List<Message> = emptyList()
    var temperature: Double? = null
    var topP: Double? = null
    var n: Int? = null
    var stream: Boolean? = null
    private val stop = mutableListOf<String>()
    var maxTokens: Int? = null
    var presencePenalty: Double? = null
    var frequencyPenalty: Double? = null
    private val logitBias = mutableMapOf<String, Int>()
    var user: String? = null

    fun stop(vararg stops: String) {
        stop.addAll(stops)
    }

    fun logitBias(vararg pairs: Pair<String, Int>) {
        pairs.forEach { (token, bias) -> logitBias[token] = bias }
    }

    fun build(): ChatCompletionRequest {
        require(messages.isNotEmpty()) { "At least one message is required" }

        return ChatCompletionRequest(
            model = llmConfig.modelName,
            messages = messages,
            temperature = temperature,
            topP = topP,
            n = n,
            stream = stream,
            stop = if (stop.isEmpty()) null else stop,
            maxTokens = maxTokens,
            presencePenalty = presencePenalty,
            frequencyPenalty = frequencyPenalty,
            logitBias = logitBias.ifEmpty { null },
            user = user,
        )
    }
}

class MessagesList {
    private val messages = mutableListOf<Message>()

    fun system(content: String) = messages.add(Message("system", content))

    fun user(content: String) = messages.add(Message("user", content))

    fun build(): List<Message> = messages
}

fun messages(block: MessagesList.() -> Unit): List<Message> {
    val list = MessagesList()
    list.block()
    return list.build()
}
