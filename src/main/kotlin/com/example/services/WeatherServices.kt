package com.example.services

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import com.example.models.CityWeatherData
import com.example.util.RedisClient
import kotlinx.serialization.json.Json
import com.example.parsers.WeatherResponseParser
import com.example.utils.RetryOperationUtil
import com.example.models.CityList
import com.example.util.EnvLoader
import kotlin.random.Random

class WeatherServices {

    private val apiKey:String = EnvLoader.get("TOMORROW_API_KEY") { it }
    private val cities = CityList.cities

    suspend fun fetchWeatherData(): List<CityWeatherData> {
        val weatherDataList = mutableListOf<CityWeatherData>()

        coroutineScope {
            cities.forEach { (cityName, _)  ->
                launch {
                    val weatherData = fetchWeather(cityName)
                    if (weatherData != null) {
                        weatherDataList.add(weatherData)
                        saveWeatherDataToRedis(weatherData,cityName)
                    }
                }
            }
        }
        println("The End")
        return weatherDataList
    }

    private suspend fun fetchWeather(city: String, maxRetries: Int = 3): CityWeatherData? {
        val url = "https://api.tomorrow.io/v4/weather/realtime?location=$city&apikey=$apiKey"

        return RetryOperationUtil.retryOperation(maxRetries) {
            if (Random.nextDouble() < 0.2) {
                saveWeatherApiErrorToRedis(city, "error by probability")
                throw Exception("The API request failed for city: $city")
            }

            try {
                makeWeatherRequest(url, city)
            } catch (e: Exception) {
                saveWeatherApiErrorToRedis(city, e.message)
                null
            }
        }
    }

    private fun makeWeatherRequest(urlString: String, city: String): CityWeatherData? {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                return WeatherResponseParser.parseWeatherResponse(city, response)
            } else {
                println("Error: $responseCode for city $city")
                saveWeatherApiErrorToRedis(city, "$responseCode in weather API")
                return null
            }
        } catch (e: Exception) {
            println("Error making HTTP request: ${e.message}")
            saveWeatherApiErrorToRedis(city, "making http request ${e.message}")
            return null
        }
    }

    private fun saveWeatherDataToRedis(cityWeatherData: CityWeatherData, cityName: String) {
        val redisClient = RedisClient.getClient()
        val key = "weather:${cityWeatherData.city.lowercase()}"
        val value = """{
        "city": "${cityName}",
        "temperature": ${cityWeatherData.temperature},
        "humidity": ${cityWeatherData.humidity},
        "precipitationProbability": ${cityWeatherData.precipitationProbability},
        "windSpeed": ${cityWeatherData.windSpeed},
        "time": "${cityWeatherData.time}"
        }"""
        redisClient.set(key, value)
        println("Saving weather data for ${cityWeatherData.city} at ${cityWeatherData.time}...")
    }

    private fun saveWeatherApiErrorToRedis(city: String, errorMessage: String?) {
        val redisClient = RedisClient.getClient()
        val timestamp = System.currentTimeMillis()
        val logEntry = """{
        "city": "$city",
        "error": "${errorMessage ?: "Unknown error"}",
        "timestamp": "$timestamp"
    }"""

        redisClient.rpush("weather:error_logs", logEntry)
        println("Error logged in Redis for city $city: ${errorMessage ?: "Unknown error"}.")
    }

    fun getWeatherByCity(city: String): CityWeatherData? {
        val jedis = RedisClient.getClient()

        val storedJsonWeatherData = jedis.get("weather:${city.lowercase()}")

        println("Retrieved data for city $city: $storedJsonWeatherData")

        return try {
            Json.decodeFromString<CityWeatherData>(storedJsonWeatherData)
        } catch (e: Exception) {
            println("Error decoding JSON for city $city: ${e.message}")
            null
        }
    }
}