package com.chesspuzzletext2sql.errors

sealed class CustomError {
  abstract val message: String
}
