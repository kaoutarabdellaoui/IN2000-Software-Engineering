package no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.datasource

import android.util.Log
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models.Locationforecast
import java.net.HttpURLConnection
import java.net.URL

/**
 * Datasource for fetching weather data from the MET API.
 */
class LocationForecastDatasource {
    /**
     * Fetches weather data from the MET API.
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return Locationforecast object or null if request fails
     */
    fun getLocationForecast(lat: Double, lon: Double): Locationforecast? {
        val urlString = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=$lat&lon=$lon"

        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "IN2000_trails/1.0 (sondrein@uio.no)")
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = Json { ignoreUnknownKeys = true }
                // Deserialize the response to a Locationforecast object
                json.decodeFromString<Locationforecast>(response)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationForecastDatasource", "Feil ved parsing av JSON: ${e.message}")
            null
        }
    }
}
