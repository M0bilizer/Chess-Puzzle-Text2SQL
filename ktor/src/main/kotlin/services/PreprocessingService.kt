package com.chesspuzzletext2sql.services

import java.io.File
import org.koin.core.component.KoinComponent

class PreprocessingService(private val promptTemplatePath: String) : KoinComponent {
    private val promptTemplate: String by lazy {
        File(promptTemplatePath).takeIf { it.exists() }?.readText()
            ?: throw IllegalArgumentException(
                "Prompt template file not found at $promptTemplatePath"
            )
    }

    fun getPromptTemplate(): String = promptTemplate
}
