package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.datasource

import android.util.Log
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models.MetAlerts
import java.net.HttpURLConnection
import java.net.URL

/**
 * Datasource for fetching MET alerts from the MET API
 */
class MetAlertsDatasource {
    /**
     * Fetches MET alerts from the MET API
     * @return METAlerts object or null if request fails
     */
    fun getMetAlerts(): MetAlerts? {
        val urlString = "https://api.met.no/weatherapi/metalerts/2.0/current.json"

        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "IN2000_trails/1.0 (sondrein@uio.no)")
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = Json { ignoreUnknownKeys = true }
                Log.d("MetAlertsDatasource", "API Response: $response")

                // Deserialize the response to a MetAlerts object
                json.decodeFromString<MetAlerts>(response)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MetAlertsDatasource", "Feil ved parsing av JSON: ${e.message}")
            null
        }
    }
}
