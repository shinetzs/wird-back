package com.example.service

import kotlinx.coroutines.*
import com.example.services.WetherServices

class TimerService {

    private val intervalMillis: Long = 5 * 60 * 1000
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val weatherServices = WetherServices()

    fun start() {
        serviceScope.launch {
            while (isActive) {
                try {
                    weatherServices.fetchWeatherData()
                } catch (e: Exception) {
                    println("Error fetching weather data: ${e.message}")
                }
                delay(intervalMillis)
            }
        }
    }
}
