package com.chess.puzzle.text2sql.web.entities.helper

interface CustomError {
    val message: String
}

sealed class GenericError : CustomError {
    data object Error : GenericError() {
        override val message: String = "Generic Error"
    }
}

sealed class CallDeepSeekError : CustomError {
    data object UnexpectedResult : CallDeepSeekError() {
        override val message: String = "Unexpected result from DeepSeek API"
    }

    data object PermissionError : CallDeepSeekError() {
        override val message: String = "Permission error from DeepSeek API"
    }

    data object InvalidRequestError : CallDeepSeekError() {
        override val message: String = "Invalid Request error from DeepSeek API"
    }

    data object HttpError : CallDeepSeekError() {
        override val message: String = "HTTP error from DeepSeek API"
    }

    data object AuthenticationError : CallDeepSeekError() {
        override val message: String = "Authentication failure with DeepSeek API"
    }

    data object IOException : CallDeepSeekError() {
        override val message: String = "IO Exception with DeepSeek API"
    }

    data object InsufficientBalanceError : CallDeepSeekError() {
        override val message: String = "Insufficient Balance for DeepSeek API"
    }

    data object ServerError : CallDeepSeekError() {
        override val message: String = "Server error from DeepSeek API"
    }

    data object RateLimitError : CallDeepSeekError() {
        override val message: String = "Rate limit error from DeepSeek API"
    }

    data object ServerOverload : CallDeepSeekError() {
        override val message: String = "DeepSeek Server is overloaded"
    }

    data class UnknownError(val statusCode: Int, val errorMessage: String) : CallDeepSeekError() {
        override val message: String = "Unknown error: $statusCode - $errorMessage"
    }
}

sealed class GetSimilarDemonstrationError : CustomError {
    data object NetworkError : GetSimilarDemonstrationError() {
        override val message: String = "Network error with Sentence Transformer Microservice"
    }

    data object InternalError : GetSimilarDemonstrationError() {
        override val message: String = "Internal error within Sentence Transformer Microservice"
    }
}

sealed class GetRandomPuzzlesError : CustomError {
    data class Throwable(val e: kotlin.Throwable) : GetRandomPuzzlesError() {
        override val message: String = "Error getting random puzzles: ${e.message}"
    }
}

sealed class ProcessPromptError : CustomError {
    data object InsufficientDemonstrationsError : ProcessPromptError() {
        override val message: String = "Not enough Demonstrations when processing prompt"
    }

    data object InvalidDemonstrationError : ProcessPromptError() {
        override val message: String = "Demonstrations are not valid when processing prompt"
    }

    data object MissingPlaceholderError : ProcessPromptError() {
        override val message: String = "Missing placeholder when processing prompt"
    }
}

sealed class ProcessQueryError : CustomError {
    data class ValidationError(val isValid: Boolean, val isAllowed: Boolean) : ProcessQueryError() {
        override val message: String =
            "Query validation failed: isValid=$isValid, isAllowed=$isAllowed"
    }

    data object HibernateError : ProcessQueryError() {
        override val message: String = "Hibernate error while processing the query"
    }
}

sealed class WriteToFileError : CustomError {
    data class Exception(val e: java.lang.Exception) : WriteToFileError() {
        override val message: String = "Error writing to file: ${e.message}"
    }
}

sealed class GetBenchmarkEntriesError : CustomError {
    data class IOException(val e: java.io.IOException) : GetBenchmarkEntriesError() {
        override val message: String = "IOException while getting benchmark entries"
    }
}

sealed class GetTextFileError : CustomError {
    data class IOException(val e: java.io.IOException) : GetTextFileError() {
        override val message: String = "IOException while loading text file"
    }

    data class UnexpectedError(val e: Exception) : GetTextFileError() {
        override val message: String = "Unexpected Error while loading text file"
    }
}
