package com.chess.puzzle.text2sql.web.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.jupiter.api.Test

class AccessControlTest {

    @Test
    fun `validate access control rules`() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val controller = Layer("Controller", "com.chess.puzzle.text2sql.web.controllers..")
                val service = Layer("Service", "com.chess.puzzle.text2sql.web.service..")
                val validator = Layer("Validator", "com.chess.puzzle.text2sql.web.validator..")
                val repositories = Layer("Controller", "com.chess.puzzle.text2sql.web.repositories..")
                val entities = Layer("Controller", "com.chess.puzzle.text2sql.web.entities..")

                controller.dependsOn(service)
                service.dependsOn(validator)
                validator.dependsOnNothing()
                repositories.dependsOn(entities)
                entities.dependsOnNothing()
            }
    }
}