package com.chess.puzzle.text2sql.web.config

import kotlin.annotation.Retention

@Retention(AnnotationRetention.RUNTIME)
annotation class CustomTimeout(val value: Long)
