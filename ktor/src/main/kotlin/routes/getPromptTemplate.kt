package com.chesspuzzletext2sql.features

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.services.PreprocessingService
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.getPromptTemplate(path: String) {
    val preprocessingService: PreprocessingService by inject()
    get(path) {
        val result = binding {
            isConnected().bind()
            val promptTemplate = preprocessingService.getPromptTemplate()
            promptTemplate
        }
        result.fold(
            failure = { err ->
                when (err) {
                    is SystemError -> call.handleSystemError(err)
                    is ClientError -> call.handleClientError(err)
                }
            },
            success = { promptTemplate -> call.respond(promptTemplate) },
        )
    }
}
