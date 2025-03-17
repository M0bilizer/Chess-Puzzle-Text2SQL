package com.chess.puzzle.text2sql.web.service.llm

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import org.springframework.stereotype.Component

/**
 * A factory class for creating instances of large language models based on the specified model
 * name.
 *
 * This factory provides a standardized way to obtain instances of LLMs (Deepseek and Mistral)
 * enabling consistent interaction regardless of the underlying model.
 */
@Component
class LargeLanguageModelFactory(private val deepSeek: Deepseek, private val mistral: Mistral) {
    /**
     * Retrieves an instance of the specified large language model.
     *
     * @param modelName The name of the model to retrieve.
     * @return An instance of [LargeLanguageModel] corresponding to the provided model name.
     * @throws IllegalArgumentException if the models name is not supported.
     */
    fun getModel(modelName: ModelName): LargeLanguageModel {
        return when (modelName) {
            ModelName.Deepseek -> deepSeek
            ModelName.Mistral -> mistral
        }
    }
}
