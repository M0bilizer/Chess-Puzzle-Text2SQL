package com.chess.puzzle.text2sql.web.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class Controller {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("message", "Welcome to the Home Page!")
        return "home"
    }
}