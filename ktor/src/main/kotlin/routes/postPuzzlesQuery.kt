package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.preprocess
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.PromptTemplate
import com.chesspuzzletext2sql.model.SupportedModel
import com.chesspuzzletext2sql.model.messages
import com.chesspuzzletext2sql.services.DatabaseService
import com.chesspuzzletext2sql.services.HTTPService
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.post
import kotlin.getValue
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.postPuzzlesQuery(path: String) {
    val httpService: HTTPService by inject()
    val databaseService: DatabaseService by inject()
    post(path) {
        val result = coroutineBinding {
            val (query, promptTemplate, llmConfig) = validateCall(call).bind()
            val chatCompletion =
                httpService
                    .callModel(llmConfig) {
                        messages = messages {
                            system("You are a text-to-SQL model")
                            user(promptTemplate(query))
                        }
                        stream = false
                    }
                    .bind()
            val sql = preprocess(chatCompletion)
            val puzzles = databaseService.fetchPuzzles(sql).bind()
            puzzles
        }

        result.fold(
            failure = { err ->
                when (err) {
                    is SystemError -> call.handleSystemError(err)
                    is ClientError -> call.handleClientError(err)
                }
            },
            success = { puzzles -> call.respond(puzzles) },
        )
    }
}

@Serializable
private data class PuzzlesQueryRequest(val query: String, val template: String, val model: String)

private data class PuzzlesQueryDto(
    val query: String,
    val promptTemplate: PromptTemplate,
    val llmConfig: LLMConfig,
) {
    companion object {
        fun from(request: PuzzlesQueryRequest, promptTemplate: PromptTemplate, config: LLMConfig) =
            PuzzlesQueryDto(request.query, promptTemplate, config)
    }
}

private suspend fun validateCall(call: RoutingCall): Result<PuzzlesQueryDto, CustomError> {
    val request = call.receive<PuzzlesQueryRequest>()
    val multipleErrors =
        ClientError.collect {
            addIf(request.query.isEmpty(), ClientError.EmptyMessage)
            addIf(request.template.isEmpty(), ClientError.EmptyTemplate)

            val promptTemplate = AvailablePromptTemplate[request.template]
            addIf(promptTemplate == null, ClientError.UnsupportedTemplate)

            val model = SupportedModel.fromProviderName(request.model)
            addIf(model == null, ClientError.UnsupportedModel)
            model?.let { addIf(AvailableModels[it] == null, ClientError.UnavailableModel) }
        }

    return if (multipleErrors.size > 0) Err(multipleErrors)
    else {
        val promptTemplate = AvailablePromptTemplate[request.template]!!
        val config = AvailableModels[SupportedModel.fromProviderName(request.model)!!]!!
        Ok(PuzzlesQueryDto.from(request, promptTemplate, config))
    }
}
