package com.chesspuzzletext2sql.config

import com.chesspuzzletext2sql.helpers.readMdFromResources

data class ApplicationConfig(
    val promptTemplate: String =
        readMdFromResources("/inferencePromptTemplate.md")
            ?: throw IllegalStateException("Inference Prompt Template is Required")
)
