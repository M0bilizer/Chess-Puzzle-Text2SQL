package com.chess.puzzle.text2sql.web.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for defining endpoints related to the Sentence Transformer service.
 *
 * This class is annotated with `@Configuration` to indicate that it provides configuration for the
 * Spring application context. It holds the base URL for the Sentence Transformer service and
 * constructs derived endpoints based on it.
 *
 * @property sentenceTransformerUrl The base URL for the Sentence Transformer service.
 * @property partialSentenceTransformerUrl The derived URL for the partial Sentence Transformer
 *   endpoint.
 */
@Configuration
data class SentenceTransformerEndpoints(
    @Value("\${sentence_transformer_url}") val sentenceTransformerUrl: String
) {
    /**
     * The derived URL for the partial Sentence Transformer endpoint.
     *
     * This property appends `/partial` to the base `sentenceTransformerUrl` to construct the
     * endpoint URL for the partial Sentence Transformer functionality.
     */
    val partialSentenceTransformerUrl: String = "$sentenceTransformerUrl/partial"
}
