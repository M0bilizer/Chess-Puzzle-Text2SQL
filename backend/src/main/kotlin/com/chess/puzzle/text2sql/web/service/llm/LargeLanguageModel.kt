package com.chess.puzzle.text2sql.web.service.llm

import com.aallam.openai.api.chat.ChatCompletion

interface LargeLanguageModel {
    suspend fun callModel(query: String): ChatCompletion
}
