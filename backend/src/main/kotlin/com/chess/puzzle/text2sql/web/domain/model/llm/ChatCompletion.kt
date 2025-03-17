package com.chess.puzzle.text2sql.web.domain.model.llm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request for generating a chat completion using a large language model (LLM).
 *
 * This class encapsulates the necessary parameters to send a request to the LLM service for
 * generating a response to a given conversation or prompt.
 *
 * @property model The identifier of the model to be used for the completion.
 * @property messages A list of messages representing the conversation history. Each message
 *   contains a role and content.
 * @property temperature Controls randomness in the model's output. Higher values make output more
 *   random, lower values make it more deterministic.
 * @property topP Limits the sampling to the most likely tokens up to the threshold. Top-P sampling
 *   is used instead of temperature when this is set.
 * @property n Specifies how many responses to generate.
 * @property stream Whether to stream the response as it is generated. Default is false.
 * @property stop Specifies conditions under which the model will stop generating tokens.
 * @property maxTokens The maximum number of tokens to generate in the response.
 * @property presencePenalty Penalizes new tokens based on their presence in the conversation
 *   history.
 * @property frequencyPenalty Penalizes new tokens based on their frequency in the conversation
 *   history.
 * @property logitBias A map of token ids to bias values, used to influence the probability of
 *   certain tokens being chosen.
 * @property user The identifier of the user sending the request, for tracking purposes.
 * @see Message
 */
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

/**
 * Represents a message in a chat conversation.
 *
 * Each message consists of a role (e.g., 'user', 'assistant') and the content of the message.
 *
 * @property role The role associated with the message, indicating the sender of the content (e.g.,
 *   'user', 'assistant').
 * @property content The actual content of the message.
 */
@Serializable
data class Message(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

/**
 * Represents the response received from the LLM service after a chat completion request.
 *
 * This class contains the generated response content, including metadata about the response and
 * usage statistics.
 *
 * @property id The unique identifier of the response.
 * @property object A string indicating the type of response.
 * @property created The timestamp (Unix epoch time) when the response was created.
 * @property model The identifier of the model used to generate the response.
 * @property choices A list of generated responses, where each choice contains the message and other
 *   metadata.
 * @property usage Statistics about the token usage for the request and response.
 * @see Choice
 * @see Usage
 */
@Serializable
data class ChatCompletionResponse(
    @SerialName("id") val id: String,
    @SerialName("object") val `object`: String,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<Choice>,
    @SerialName("usage") val usage: Usage,
)

/**
 * Represents a generated response choice in a chat completion.
 *
 * Each choice represents one possible way the model could respond to the input request, including
 * metadata about the generation process.
 *
 * @property index The index position of the choice in the list of responses.
 * @property message The generated message for this choice, containing the content and the role.
 * @property finishReason The reason why the response generation process finished (e.g., 'stop',
 *   'length', 'content_filter').
 * @see Message
 */
@Serializable
data class Choice(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: Message,
    @SerialName("finish_reason") val finishReason: String,
)

/**
 * Represents usage statistics for a chat completion request and response.
 *
 * This class tracks the number of tokens used in the prompt, the completion, and the total tokens
 * used overall.
 *
 * @property promptTokens The number of tokens used in the prompt.
 * @property completionTokens The number of tokens used in the completion.
 * @property totalTokens The total number of tokens used (prompt + completion).
 */
@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
)
