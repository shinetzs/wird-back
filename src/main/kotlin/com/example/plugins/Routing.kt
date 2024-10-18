package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import weatherRouting
import io.ktor.server.plugins.swagger.*

fun Application.configureRouting() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        get("/") {
            call.respondText("Bienvenidos a la aplicación del Tiempo!")
        }
        weatherRouting()
    }
}
