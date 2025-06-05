package com.chesspuzzletext2sql.config

import com.chesspuzzletext2sql.helpers.readMdFromResources

object ApplicationConfig {
    val PROMPT_TEMPLATE =
        readMdFromResources("/inferencePromptTemplate.md")
            ?: throw IllegalStateException("Inference Prompt Template is Required")
}
