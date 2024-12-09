package com.chess.puzzle.text2sql.web.entities

data class PromptTemplate(var template: String, var maskedQuery: String, var query: String)
