package com.chess.puzzle.text2sql.web.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class SentenceTransformerEndpoints(
    @Value("\${sentence_transformer_url}") val sentenceTransformerUrl: String
) {
    val partialSentenceTransformerUrl: String = "$sentenceTransformerUrl/partial"
}
