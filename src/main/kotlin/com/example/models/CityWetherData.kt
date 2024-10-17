package com.example.models;

import kotlinx.serialization.Serializable;

@Serializable
data class CityWeatherData(
    val city: String,
    val temperature: Double?,
    val humidity: Double?,
    val precipitationProbability: Double?,
    val windSpeed: Double?,
    val time:String?
)
