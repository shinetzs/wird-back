package com.example.util

import java.io.File

object EnvLoader {
    private val envMap = mutableMapOf<String, String>()

    init {
        loadEnvFile()
    }

    private fun loadEnvFile() {
        val envFile = File(".env")

        if (envFile.exists()) {
            envFile.readLines().forEach { line ->
                if (line.isNotEmpty() && !line.startsWith("#")) {
                    val (key, value) = line.split("=", limit = 2)
                    envMap[key.trim()] = value.trim()
                }
            }
        }
    }

    fun <T> get(key: String, transform: (String) -> T): T {
        val value = envMap[key] ?: System.getenv(key) ?: throw IllegalArgumentException("$key is missing")
        return transform(value)
    }
}
