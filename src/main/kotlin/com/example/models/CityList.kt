package com.example.models

data class City(val name: String, val code: String)

//para ser mas exacto se podria hacer con coordenadas
object CityList {
    val cities = listOf(
        City("Santiago", "CL"),
        City("Zurich", "CH"),
        City("Auckland", "NZ"),
        City("Sydney", "AU"),
        City("London", "UK"),
        City("Georgia", "US")
    )
}