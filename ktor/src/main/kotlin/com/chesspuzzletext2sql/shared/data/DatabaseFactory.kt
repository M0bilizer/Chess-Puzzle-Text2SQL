package com.chesspuzzletext2sql.shared.data

import com.chesspuzzletext2sql.config.EnvironmentConfig
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun createDatabase(config: EnvironmentConfig.DatabaseConfig): Database =
        Database.connect(
                url = config.url,
                driver = config.driver,
                user = config.user,
                password = config.password,
            )
            .also { println("✅ Database connected: ${config.url}") }
}
