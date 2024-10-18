package com.example.utils

import kotlinx.coroutines.delay

//función genérica para reintentar la ejecución de una función X cantidad de veces
//ademas se agrega el posible fallo del 20%
object RetryOperationUtil {
    suspend fun <T> retryOperation(maxRetries: Int, delayMillis: Long = 1000L, function: suspend () -> T?): T? {
        var attempts = 0
        var result: T? = null

        while (attempts < maxRetries) {
            try {
                result = function()
                if (result != null) break
            } catch (e: Exception) {
                attempts++
                println("Attempt $attempts failed: ${e.message}")
                if (attempts >= maxRetries) {
                    println("Max retries reached.")
                    break
                }
                delay(delayMillis)
            }
        }

        return result
    }
}
