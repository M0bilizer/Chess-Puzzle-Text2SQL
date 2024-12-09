package com.chess.puzzle.text2sql.web.entities.helper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Property {
    @Value("\${api_key}")
    lateinit var apiKey: String

    @Value("\${base_url}")
    lateinit var baseUrl: String

    @Value("\${sentence_transformer_url}")
    lateinit var sentenceTransformerUrl: String

    val sentenceTransformerPartialUrl: String
        get() = "$sentenceTransformerUrl/partial"
}
