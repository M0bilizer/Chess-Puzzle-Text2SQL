package com.chess.puzzle.text2sql.web.entities.helper

/**
 * Interface representing a custom error with a message.
 *
 * This interface is implemented by various error classes to provide a consistent way to retrieve
 * error messages.
 */
interface CustomError {
    /** The error message associated with the error. */
    val message: String
}

/** A sealed class representing generic errors. */
sealed class GenericError : CustomError {
    /** Represents a generic error with a default message. */
    data object Error : GenericError() {
        override val message: String = "Generic Error"
    }
}

/** A sealed class representing errors related to calling the DeepSeek API. */
sealed class CallDeepSeekError : CustomError {
    /** Represents an unexpected result from the DeepSeek API. */
    data object UnexpectedResult : CallDeepSeekError() {
        override val message: String = "Unexpected result from DeepSeek API"
    }

    /** Represents a permission error from the DeepSeek API. */
    data object PermissionError : CallDeepSeekError() {
        override val message: String = "Permission error from DeepSeek API"
    }

    /** Represents an invalid request error from the DeepSeek API. */
    data object InvalidRequestError : CallDeepSeekError() {
        override val message: String = "Invalid Request error from DeepSeek API"
    }

    /** Represents an HTTP error from the DeepSeek API. */
    data object HttpError : CallDeepSeekError() {
        override val message: String = "HTTP error from DeepSeek API"
    }

    /** Represents an authentication failure with the DeepSeek API. */
    data object AuthenticationError : CallDeepSeekError() {
        override val message: String = "Authentication failure with DeepSeek API"
    }

    /** Represents an IO exception when interacting with the DeepSeek API. */
    data object IOException : CallDeepSeekError() {
        override val message: String = "IO Exception with DeepSeek API"
    }

    /** Represents an insufficient balance error for the DeepSeek API. */
    data object InsufficientBalanceError : CallDeepSeekError() {
        override val message: String = "Insufficient Balance for DeepSeek API"
    }

    /** Represents a server error from the DeepSeek API. */
    data object ServerError : CallDeepSeekError() {
        override val message: String = "Server error from DeepSeek API"
    }

    /** Represents a rate limit error from the DeepSeek API. */
    data object RateLimitError : CallDeepSeekError() {
        override val message: String = "Rate limit error from DeepSeek API"
    }

    /** Represents an error when the DeepSeek server is overloaded. */
    data object ServerOverload : CallDeepSeekError() {
        override val message: String = "DeepSeek Server is overloaded"
    }

    /**
     * Represents an unknown error with a status code and error message.
     *
     * @property statusCode The HTTP status code associated with the error.
     * @property errorMessage The error message returned by the API.
     */
    data class UnknownError(val statusCode: Int, val errorMessage: String) : CallDeepSeekError() {
        override val message: String = "Unknown error: $statusCode - $errorMessage"
    }
}

/**
 * A sealed class representing errors related to getting similar demonstrations from the Sentence
 * Transformer microservice.
 */
sealed class GetSimilarDemonstrationError : CustomError {
    /** Represents a network error when interacting with the Sentence Transformer microservice. */
    data object NetworkError : GetSimilarDemonstrationError() {
        override val message: String = "Network error with Sentence Transformer Microservice"
    }

    /** Represents an internal error within the Sentence Transformer microservice. */
    data object InternalError : GetSimilarDemonstrationError() {
        override val message: String = "Internal error within Sentence Transformer Microservice"
    }
}

/** A sealed class representing errors related to retrieving random puzzles. */
sealed class GetRandomPuzzlesError : CustomError {
    /**
     * Represents an error caused by a throwable when retrieving random puzzles.
     *
     * @property e The throwable that caused the error.
     */
    data class Throwable(val e: kotlin.Throwable) : GetRandomPuzzlesError() {
        override val message: String = "Error getting random puzzles: ${e.message}"
    }
}

/** A sealed class representing errors related to processing prompts. */
sealed class ProcessPromptError : CustomError {
    /** Represents an error when there are insufficient demonstrations to process the prompt. */
    data object InsufficientDemonstrationsError : ProcessPromptError() {
        override val message: String = "Not enough Demonstrations when processing prompt"
    }

    /** Represents an error when the demonstrations are invalid for processing the prompt. */
    data object InvalidDemonstrationError : ProcessPromptError() {
        override val message: String = "Demonstrations are not valid when processing prompt"
    }

    /** Represents an error when a placeholder is missing while processing the prompt. */
    data object MissingPlaceholderError : ProcessPromptError() {
        override val message: String = "Missing placeholder when processing prompt"
    }
}

/** A sealed class representing errors related to processing SQL queries. */
sealed class ProcessQueryError : CustomError {
    /**
     * Represents a validation error when processing a query.
     *
     * @property isValid Indicates whether the query is valid.
     * @property isAllowed Indicates whether the query is allowed.
     */
    data class ValidationError(val isValid: Boolean, val isAllowed: Boolean) : ProcessQueryError() {
        override val message: String =
            "Query validation failed: isValid=$isValid, isAllowed=$isAllowed"
    }

    /** Represents a Hibernate error when processing a query. */
    data object HibernateError : ProcessQueryError() {
        override val message: String = "Hibernate error while processing the query"
    }
}

/** A sealed class representing errors related to writing to a file. */
sealed class WriteToFileError : CustomError {
    /**
     * Represents an error caused by an exception when writing to a file.
     *
     * @property e The exception that caused the error.
     */
    data class Exception(val e: java.lang.Exception) : WriteToFileError() {
        override val message: String = "Error writing to file: ${e.message}"
    }
}

/** A sealed class representing errors related to retrieving benchmark entries. */
sealed class GetBenchmarkEntriesError : CustomError {
    /**
     * Represents an IO exception when retrieving benchmark entries.
     *
     * @property e The IO exception that caused the error.
     */
    data class IOException(val e: java.io.IOException) : GetBenchmarkEntriesError() {
        override val message: String = "IOException while getting benchmark entries"
    }

    /** Represents an error when the benchmark file cannot be found. */
    data object FileNotFoundError : GetBenchmarkEntriesError() {
        override val message: String = "Cannot find file while loading json file"
    }
}

/** A sealed class representing errors related to retrieving text files. */
sealed class GetTextFileError : CustomError {
    /**
     * Represents an IO exception when retrieving a text file.
     *
     * @property e The IO exception that caused the error.
     */
    data class IOException(val e: java.io.IOException) : GetTextFileError() {
        override val message: String = "IOException while loading text file"
    }

    /** Represents an error when the text file cannot be found. */
    data object FileNotFoundError : GetTextFileError() {
        override val message: String = "Cannot find file while loading text file"
    }
}
