package com.chesspuzzletext2sql.features.puzzleSearch.models

import kotlinx.serialization.Serializable

@Serializable
data class PromptTemplate(
    val instruction: String,
    val context: String,
    val demonstrations: List<String>,
    val input: String,
) {
    init {
        require(input.contains("\${input}"))
    }

    operator fun invoke(userInput: String): String =
        """
        | #Instruction
        | $instruction
        | 
        | #Context
        | $context
        | 
        | #Demonstrations
        | ${demonstrations.joinToString("\n")}
        | 
        | #Input
        | ${input.replace("\${input}", userInput)}
    """
            .trimMargin("| ")
}
