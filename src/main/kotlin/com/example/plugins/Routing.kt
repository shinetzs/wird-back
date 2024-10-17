package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import wetherRouting

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Bienvenidos a la aplicación del Tiempo!")
        }
        wetherRouting()
    }
}
