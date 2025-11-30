package com.chesspuzzletext2sql.shared.validator

import com.chesspuzzletext2sql.errors.Fail
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import dev.nesk.akkurate.Validator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Complete validation pipeline that combines:
 * 1. JSON structure validation (using reflection)
 * 2. Business logic validation (using Akkurate)
 * 3. Transformation to DTO
 */
inline fun <reified A : Any, C> validateRequest(
    jsonElement: JsonElement,
    validator: Validator.Runner<A>,
    transformer: (A) -> C,
    json: Json = Json.Default,
): Result<C, Fail> =
    // Step 1: Validate JSON structure and parse
    parseJson<A>(jsonElement, json)
        // Step 2: Validate business logic with Akkurate
        .andThen { request -> validateBusinessLogic(request, validator) }
        // Step 3: Transform to DTO
        .andThen { validatedRequest -> transformToDto(validatedRequest, transformer) }
