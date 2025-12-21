package com.chesspuzzletext2sql.features.puzzleSearch.data

import com.chesspuzzletext2sql.features.puzzleSearch.models.PromptTemplate

interface TemplateRepository {
    fun getTemplate(name: String): PromptTemplate?

    fun getDefault(): PromptTemplate

    fun templateExists(name: String): Boolean
}

class TemplateRepositoryImp(
    private val templates: Map<String, PromptTemplate>,
    private val default: String,
) : TemplateRepository {
    init {
        require(default in templates.keys) {
            "Default prompt template '$default' must be present in templates"
        }
    }

    override fun getTemplate(name: String) = templates[name]

    override fun getDefault() = templates[default]!!

    override fun templateExists(name: String) = name in templates
}
