package com.chesspuzzletext2sql.features.prompts.data

import com.chesspuzzletext2sql.features.prompts.models.PromptTemplate
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching

interface TemplateRepository {
    fun getTemplate(name: String): Result<PromptTemplate, Throwable>

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

    override fun getTemplate(name: String): Result<PromptTemplate, Throwable> = runCatching {
        templates[name] ?: throw NoSuchElementException("Template '$name' not found")
    }

    override fun getDefault(): PromptTemplate = templates[default]!!

    override fun templateExists(name: String): Boolean = name in templates
}
