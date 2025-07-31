package com.chesspuzzletext2sql.model

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

    operator fun invoke(userInput: String): String = """
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
    """.trimMargin("| ")
}

object AvailablePromptTemplate {
    private val storage = mutableMapOf<String, PromptTemplate>()

    operator fun get(name: String): PromptTemplate? = storage[name]

    val all: Map<String, PromptTemplate>
        get() = storage.toMap()

    internal fun update(configs: Map<String, PromptTemplate>) {
        require(configs.isNotEmpty()) { "Must have at least one prompt template" }
        storage.clear()
        storage.putAll(configs)
    }
}
