package com.chess.puzzle.text2sql.web.service.llm

import com.chess.puzzle.text2sql.web.domain.model.ModelName

class LargeLanguageModelFactory(private val deepSeek: DeepSeek, private val mistral: Mistral) {
    fun getModel(modelName: ModelName): LargeLanguageModel {
        return when (modelName) {
            ModelName.Default -> deepSeek
            ModelName.Alternative -> mistral
        }
    }
}
