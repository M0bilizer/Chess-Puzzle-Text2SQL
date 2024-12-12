package com.chess.puzzle.text2sql.web.entities.helper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Represents configuration properties loaded from application.properties.
 *
 * A service or class that needs to use the properties should autowire [Property].
 */
@Component
class Property {
    /**
     * Api key for DeepSeek.
     *
     * DeepSeek is the large language model used for generating SQL statements. This API key is
     * required to authenticate requests to the DeepSeek API. Used by
     * [com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper].
     */
    @Value("\${api_key}") lateinit var apiKey: String

    /**
     * DeepSeek Url for the HTTP Client.
     *
     * DeepSeek is the large language model used for generating SQL statements. This URL is the base
     * endpoint for making requests to the DeepSeek API. Example: "https://api.deepseek.com/v1" Used
     * by [com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper].
     */
    @Value("\${base_url}") lateinit var baseUrl: String

    /**
     * Sentence Transformer Microservice Url for the HTTP Client. This points to the partial
     * endpoint.
     *
     * Sentence Transformer Microservice is a service to support the Text2SQL process. Used by
     * [com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper].
     */
    @Value("\${sentence_transformer_url}") lateinit var sentenceTransformerUrl: String

    /**
     * Sentence Transformer Microservice Url for the HTTP Client. This points to the partial
     * endpoint
     *
     * Sentence Transformer Microservice is a service to support the Text2SQL process. The partial
     * endpoint does not involve schema masking. It is used for benchmarking purposes, to test
     * whether schema masking supports in improving the Text2SQL models. Used by
     * [com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper]
     */
    val sentenceTransformerPartialUrl: String
        get() = "$sentenceTransformerUrl/partial"
}
