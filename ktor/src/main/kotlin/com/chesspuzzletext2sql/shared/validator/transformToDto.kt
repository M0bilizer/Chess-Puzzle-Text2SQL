package com.chesspuzzletext2sql.shared.validator

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidRequestDetail
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching

inline fun <reified A, C> transformToDto(request: A, transform: (A) -> C): Result<C, Fail> =
    runCatching { transform(request) }
        .mapError { error ->
            Fail.InvalidRequest(
                listOf(
                    InvalidRequestDetail(
                        "transformation",
                        InvalidRequestMessage.TypeMismatch(
                            "transformation",
                            "Failed to transform request to DTO: ${error.message}",
                            "Valid transformation from ${A::class.simpleName} to DTO",
                        ),
                    )
                )
            )
        }
