package com.chesspuzzletext2sql.validators

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidRequestDetail
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import com.chesspuzzletext2sql.helpers.mapViolationsToErrors
import com.chesspuzzletext2sql.helpers.validateJsonStructure
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.nesk.akkurate.ValidationResult
import dev.nesk.akkurate.Validator
import io.ktor.server.request.receiveText
import io.ktor.server.routing.RoutingContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

data class ValidationConfig<A : Any, C>(
    val validator: Validator.Runner<A>,
    val transform: (A) -> C,
)

class RequestValidator(val json: Json = Json.Default) {

    inline fun <reified A : Any, C> validate(
        jsonText: String,
        validationConfig: ValidationConfig<A, C>,
    ): Result<C, Fail> {
        // Step 1: Validate JSON structure
        val jsonElementResult = validateJson<A>(jsonText)
        if (jsonElementResult.isErr) return Err(jsonElementResult.error)

        // Step 2: Parse JSON
        val requestResult = parseJson<A>(jsonElementResult.value)
        if (requestResult.isErr) return Err(requestResult.error)

        // Step 3: Validate business logic
        val validatedRequestResult =
            validateBusinessLogic(requestResult.value, validationConfig.validator)
        if (validatedRequestResult.isErr) return Err(validatedRequestResult.error)

        // Step 4: Transform to DTO
        return try {
            Ok(validationConfig.transform(validatedRequestResult.value))
        } catch (e: Exception) {
            Err(
                Fail.InvalidRequest(
                    listOf(
                        InvalidRequestDetail(
                            "transformation",
                            InvalidRequestMessage.TypeMismatch(
                                "transformation",
                                "Failed to transform request to DTO",
                                "Valid transformation",
                            ),
                        )
                    )
                )
            )
        }
    }

    inline fun <reified A : Any> validateJson(text: String): Result<JsonElement, Fail> {
        return try {
            val jsonElement = json.parseToJsonElement(text)
            validateJsonStructure(jsonElement, A::class)?.let { error -> Err(error) }
                ?: Ok(jsonElement)
        } catch (e: Exception) {
            Err(Fail.MalformedJson)
        }
    }

    inline fun <reified A : Any> parseJson(element: JsonElement): Result<A, Fail> {
        return try {
            Ok(json.decodeFromJsonElement(element))
        } catch (e: SerializationException) {
            Err(Fail.MalformedJson)
        } catch (e: Exception) {
            // Handle other parsing errors
            Err(
                Fail.InvalidRequest(
                    listOf(
                        InvalidRequestDetail(
                            "parsing",
                            InvalidRequestMessage.TypeMismatch(
                                "parsing",
                                "Failed to parse JSON",
                                "Valid JSON structure",
                            ),
                        )
                    )
                )
            )
        }
    }

    fun <A> validateBusinessLogic(request: A, validator: Validator.Runner<A>): Result<A, Fail> {
        return when (val result = validator(request)) {
            is ValidationResult.Success -> Ok(result.value)
            is ValidationResult.Failure -> Err(mapViolationsToErrors(result.violations))
        }
    }
}

suspend inline fun <reified A : Any, C> RoutingContext.validateRequest(
    validationConfig: ValidationConfig<A, C>
): Result<C, Fail> {
    val requestValidator = RequestValidator()
    val jsonText = call.receiveText()
    return requestValidator.validate(jsonText, validationConfig)
}
