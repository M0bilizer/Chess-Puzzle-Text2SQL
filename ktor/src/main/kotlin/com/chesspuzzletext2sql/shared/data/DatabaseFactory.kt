package com.chesspuzzletext2sql.shared.data

import com.chesspuzzletext2sql.shared.config.EnvironmentConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    fun createDatabase(config: EnvironmentConfig.DatabaseConfig): Database {
        val db =
            Database.connect(
                url = config.url,
                driver = config.driver,
                user = config.user,
                password = config.password,
            )

        return try {
            transaction(db) { exec("SELECT 1") }
            println("✅ Database connected: ${config.url}")
            db
        } catch (e: Exception) {
            println("❌ Database connection failed: ${config.url}")
            throw e
        }
    }
}
