import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.services.WeatherServices

fun Route.weatherRouting() {
    val weatherServices = WeatherServices()

    route("/weather") {
        get("/{city}") {
            val city = call.parameters["city"] ?: return@get call.respondText("City not provided", status = HttpStatusCode.BadRequest)
            val weatherData = weatherServices.getWeatherByCity(city)

            if (weatherData != null) {
                call.respond(weatherData)
            } else {
                call.respondText("Weather data not found for city: $city", status = HttpStatusCode.NotFound)
            }
        }
    }

}