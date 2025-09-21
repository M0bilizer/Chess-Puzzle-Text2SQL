package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.InvalidParameterMessage.CustomConstraint
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.PromptTemplate
import com.chesspuzzletext2sql.routes.validation.accessors.template
import com.chesspuzzletext2sql.services.QueryParsers
import com.chesspuzzletext2sql.services.QueryValidationConfig
import com.chesspuzzletext2sql.services.validateQuery
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.builders.isNotEmpty
import dev.nesk.akkurate.constraints.constrain
import dev.nesk.akkurate.constraints.otherwise
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

@Serializable @Validate data class PromptTemplateRequest(val template: String)

@Serializable data class PromptTemplateDto(val promptTemplate: PromptTemplate)

val promptTemplateConfig =
  QueryValidationConfig(
    validator =
      Validator<PromptTemplateRequest> {
        val (isValidTemplate) = template.isNotEmpty()
        if (isValidTemplate) {
          template.constrain { AvailablePromptTemplate[it] != null } otherwise
            {
              CustomConstraint.UnsupportedTemplate.code
            }
        }
      },
    transform = { request: PromptTemplateRequest ->
      PromptTemplateDto(AvailablePromptTemplate[request.template]!!)
    },
    parser = { params ->
      PromptTemplateRequest(template = QueryParsers.stringParser("template", "default")(params))
    },
  )

fun Route.getPromptTemplate(path: String) {
  get(path) {
    val result = binding {
      val promptTemplate = validateQuery(promptTemplateConfig).bind()
      promptTemplate
    }
    result.fold(
      failure = { err -> call.handleClientError(err) },
      success = { promptTemplate -> call.respond(promptTemplate) },
    )
  }
}
