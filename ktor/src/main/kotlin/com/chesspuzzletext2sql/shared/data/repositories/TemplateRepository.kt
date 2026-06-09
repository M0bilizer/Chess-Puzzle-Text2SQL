package com.chesspuzzletext2sql.shared.data.repositories

import com.chesspuzzletext2sql.features.puzzles.domains.PromptTemplate

class TemplateRepository(
    private val templates: Map<String, PromptTemplate>,
    private val default: String,
) {
    init {
        require(default in templates.keys) {
            "Default prompt template '$default' must be present in templates"
        }
    }

    fun getTemplate(name: String) = templates[name]

    fun getDefault() = templates[default]!!

    fun templateExists(name: String) = name in templates
}
