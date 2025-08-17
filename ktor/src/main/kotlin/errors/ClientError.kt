package com.chesspuzzletext2sql.errors

import com.chesspuzzletext2sql.errors.ClientError.MultipleErrors

sealed class ClientError(override val message: String) : CustomError() {
  object InvalidCount : ClientError("Count must be positive")

  object EmptyMessage : ClientError("Message cannot be empty")

  object UnsupportedModel : ClientError("Unsupported model")

  object UnavailableModel : ClientError("Model is unavailable")

  object EmptyQuery : ClientError("Query cannot be empty")

  object InvalidQuery : ClientError("Query is invalid")

  object UnallowedQuery : ClientError("Query is not allowed")

  object EmptyTemplate : ClientError("Template cannot be empty")

  object UnsupportedTemplate : ClientError("Unsupported template")

  class MultipleErrors private constructor(val errors: List<ClientError>) :
    ClientError("Multiple validation errors occurred") {
    val size: Int
      get() = errors.size

    operator fun get(index: Int): ClientError = errors[index]

    fun iterator(): Iterator<ClientError> = errors.iterator()

    companion object {
      internal fun create(errors: List<ClientError>): MultipleErrors {
        return MultipleErrors(errors)
      }
    }
  }

  companion object {
    fun collect(block: ClientErrorCollector.() -> Unit): MultipleErrors {
      return ClientErrorCollector().apply(block).build()
    }
  }
}

class ClientErrorCollector internal constructor() {
  private val errors = mutableListOf<ClientError>()

  fun addIf(condition: Boolean, error: ClientError): ClientErrorCollector {
    if (condition) {
      errors.add(error)
    }
    return this
  }

  fun add(error: ClientError): ClientErrorCollector {
    errors.add(error)
    return this
  }

  fun build(): MultipleErrors {
    return MultipleErrors.create(errors)
  }
}
