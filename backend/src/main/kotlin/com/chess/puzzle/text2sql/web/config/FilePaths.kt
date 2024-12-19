package com.chess.puzzle.text2sql.web.config

import com.chess.puzzle.text2sql.web.entities.ModelName
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class FilePaths(
    @Value("\${benchmark.json.path}") val jsonPath: String,
    @Value("\${promptTemplate.md.path}") val promptTemplateMdPath: String,
    @Value("\${baselinePromptTemplate.md.path}") val baselinePromptTemplateMdPath: String,
) {
    fun getPromptTemplate(modelName: ModelName): String {
        return when (modelName) {
            ModelName.Full -> promptTemplateMdPath
            ModelName.Partial -> promptTemplateMdPath
            ModelName.Baseline -> baselinePromptTemplateMdPath
        }
    }
}
