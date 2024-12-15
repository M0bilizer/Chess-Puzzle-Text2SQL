package com.chess.puzzle.text2sql.web.entities.helper

interface CustomError {
    val message: String
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

sealed class ProcessPromptError : CustomError {
    data object CannotFindLayout : ProcessPromptError() {
        override val message: String = "Cannot find layout for the prompt"
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
