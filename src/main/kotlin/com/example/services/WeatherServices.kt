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
import redis.clients.jedis.exceptions.JedisConnectionException
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
        try {
            redisClient.set(key, value)
            println("Saving weather data for ${cityWeatherData.city} at ${cityWeatherData.time}...")
        } catch (e: JedisConnectionException) {
            println("Error connecting to Redis to save weather : ${e.message}")
        } catch (e: Exception) {
            println("An unexpected error occurred while saving weather to Redis: ${e.message}")
        }

    }

    private fun saveWeatherApiErrorToRedis(city: String, errorMessage: String?) {
        val redisClient = RedisClient.getClient()
        val timestamp = System.currentTimeMillis()
        val logEntry = """{
        "city": "$city",
        "error": "${errorMessage ?: "Unknown error"}",
        "timestamp": "$timestamp"
    }"""

        try {
            redisClient.rpush("weather:error_logs", logEntry)
            println("Saving error logged in Redis for city $city: ${errorMessage ?: "Unknown error"}.")
        } catch (e: JedisConnectionException) {
            println("Error connecting to Redis to save error log: ${e.message}")
        } catch (e: Exception) {
            println("An unexpected error occurred while saving error log to Redis: ${e.message}")
        }
    }

    fun getWeatherByCity(city: String): CityWeatherData? {
        val jedis = RedisClient.getClient()

        return try {
            val storedJsonWeatherData = jedis.get("weather:${city.lowercase()}")

            println("Retrieved data for city $city: $storedJsonWeatherData")

            if (storedJsonWeatherData.isNullOrEmpty()) {
                println("No data found for city $city in Redis.")
                null
            } else {
                Json.decodeFromString<CityWeatherData>(storedJsonWeatherData)
            }
        } catch (e: JedisConnectionException) {
            println("Redis connection error: ${e.message}")
            null
        } catch (e: Exception) {
            println("Error decoding JSON for city $city: ${e.message}")
            null
        } finally {
            jedis.close()
        }
    }
}