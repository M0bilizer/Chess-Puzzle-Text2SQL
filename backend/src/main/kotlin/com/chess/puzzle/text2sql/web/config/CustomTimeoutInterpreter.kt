package com.chess.puzzle.text2sql.web.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

class CustomTimeoutInterpreter : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method
            val customTimeout = method.getAnnotation(CustomTimeout::class.java)
            if (customTimeout != null) {
                request.setAttribute(
                    "org.springframework.web.context.request.async.WebAsyncManager.TIMEOUT_MANAGER",
                    customTimeout.value,
                )
            }
        }
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?,
    ) {
        // Reset the timeout to the default value if needed
    }
}
