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

class WetherServices {

    //***deberia ir en un archivo aparte ignorado por git***
    private val apiKey = "0wimoMYLc5QurVKpdUQWrIAqPPF3rnuY"
    private val cities = CityList.cities

    suspend fun fetchWeatherData(): List<CityWeatherData> {
        val weatherDataList = mutableListOf<CityWeatherData>()

        coroutineScope {
            cities.forEach { (cityName, _)  ->
                launch {
                    val weatherData = fetchWeather(cityName)
                    if (weatherData != null) {
                        weatherDataList.add(weatherData)
                    }
                }
            }
        }
        return weatherDataList
    }

    suspend fun fetchWeather(city: String, maxRetries: Int = 3): CityWeatherData? {
        val url = "https://api.tomorrow.io/v4/weather/realtime?location=$city&apikey=$apiKey"

        return RetryOperationUtil.retryOperation(maxRetries) {
            makeWetherRequest(url, city)
        }
    }

    private fun makeWetherRequest(urlString: String, city: String): CityWeatherData? {
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
                return null
            }
        } catch (e: Exception) {
            println("Error making HTTP request: ${e.message}")
            return null
        }
    }

    fun getWeatherByCity(city: String): CityWeatherData? {
        val jedis = RedisClient.getClient()

        val storedJsonWeatherData = jedis.get("weather:$city")

        return storedJsonWeatherData?.let {
            Json.decodeFromString<CityWeatherData>(it)
        }
    }
}