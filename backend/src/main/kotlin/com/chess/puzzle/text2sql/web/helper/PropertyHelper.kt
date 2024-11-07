package com.chess.puzzle.text2sql.web.helper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PropertyHelper {
    @Value("\${api_key}")
    lateinit var apiKey: String

    @Value("\${base_url}")
    lateinit var baseUrl: String
}
