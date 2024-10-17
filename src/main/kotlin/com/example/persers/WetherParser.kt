package com.example.parsers

import com.example.models.CityWeatherData
import org.json.JSONObject


object WeatherResponseParser {
    fun parseWeatherResponse(city: String, response: String): CityWeatherData? {
        val jsonObject = JSONObject(response)
        val weatherData = jsonObject.getJSONObject("data")
        val values = weatherData.getJSONObject("values")

        val time = weatherData.getString("time")
        val formattedTime = DateUtils.formatUtcToDesired(time)

        return CityWeatherData(
            city = city,
            temperature = values.optDouble("temperature"),
            humidity = values.optDouble("humidity"),
            precipitationProbability = values.optDouble("precipitationProbability"),
            windSpeed = values.optDouble("windSpeed"),
            time = formattedTime
        )
    }
}