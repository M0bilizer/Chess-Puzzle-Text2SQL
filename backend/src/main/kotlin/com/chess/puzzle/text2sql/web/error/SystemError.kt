package com.chess.puzzle.text2sql.web.error

/**
 * Interface representing a system error with a message. A system error is when the application is
 * not able to process the client's request.
 *
 * This interface is implemented by various error classes to provide a consistent way to retrieve
 * error messages.
 */
interface SystemError {
    /** The error message associated with the error. */
    val message: String
}

/** A sealed class representing generic errors. */
sealed class GenericError : SystemError {
    /** Represents a generic error with a default message. */
    data object Error : GenericError() {
        override val message: String = "Generic Error"
    }
}

/** A sealed class representing errors related to calling the LLM API. */
sealed class CallLargeLanguageModelError : SystemError {
    /** Represents an unexpected result from the LLM API. */
    data object UnexpectedResult : CallLargeLanguageModelError() {
        override val message: String = "Unexpected result from LLM API"
    }

    /** Represents a permission error from the LLM API. */
    data object PermissionError : CallLargeLanguageModelError() {
        override val message: String = "Permission error from LLM API"
    }

    /** Represents an invalid request error from the LLM API. */
    data object InvalidRequestError : CallLargeLanguageModelError() {
        override val message: String = "Invalid Request error from LLM API"
    }

    /** Represents an HTTP error from the LLM API. */
    data object HttpError : CallLargeLanguageModelError() {
        override val message: String = "HTTP error from LLM API"
    }

    /** Represents an authentication failure with the LLM API. */
    data object AuthenticationError : CallLargeLanguageModelError() {
        override val message: String = "Authentication failure with LLM API"
    }

    /** Represents an IO exception when interacting with the LLM API. */
    data object IOException : CallLargeLanguageModelError() {
        override val message: String = "IO Exception with LLM API"
    }

    /** Represents an insufficient balance error for the LLM API. */
    data object InsufficientBalanceError : CallLargeLanguageModelError() {
        override val message: String = "Insufficient Balance for LLM API"
    }

    /** Represents a server error from the LLM API. */
    data object ServerError : CallLargeLanguageModelError() {
        override val message: String = "Server error from LLM API"
    }

    /** Represents a rate limit error from the LLM API. */
    data object RateLimitError : CallLargeLanguageModelError() {
        override val message: String = "Rate limit error from LLM API"
    }

    /** Represents an error when the LLM server is overloaded. */
    data object ServerOverload : CallLargeLanguageModelError() {
        override val message: String = "LLM Server is overloaded"
    }

    /**
     * Represents an unknown status code error.
     *
     * @property statusCode The unknown HTTP status code associated with the error.
     */
    data class UnknownStatusError(val statusCode: Int) : CallLargeLanguageModelError() {
        override val message: String = "Unknown status error: $statusCode"
    }

    /**
     * Represents an unknown error with a status code and error message.
     *
     * @property statusCode The HTTP status code associated with the error.
     * @property errorMessage The error message returned by the API.
     */
    data class UnknownError(val statusCode: Int, val errorMessage: String) :
        CallLargeLanguageModelError() {
        override val message: String = "Unknown error: $statusCode - $errorMessage"
    }
}

/**
 * A sealed class representing errors related to getting similar demonstrations from the Sentence
 * Transformer microservice.
 */
sealed class GetSimilarDemonstrationError : SystemError {
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
sealed class GetRandomPuzzlesError : SystemError {
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
sealed class ProcessPromptError : SystemError {
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
sealed class ProcessQueryError : SystemError {
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
sealed class WriteToFileError : SystemError {
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
sealed class GetBenchmarkEntriesError : SystemError {
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
sealed class GetTextFileError : SystemError {
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
