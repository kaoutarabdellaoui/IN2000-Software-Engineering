package no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.repository

import no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.datasource.LocationForecastDatasource
import no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models.Locationforecast

/**
 * Repository for fetching weather data.
 */
class LocationForecastRepository() {
    private val datasource = LocationForecastDatasource()

    /**
     * Gets the weather forecast for a specific location.
     * @param lat latitude of location.
     * @param lon longitude of location.
     * @return Locationforecast object or null if request fails.
     */
    fun getForecast(lat: Double, lon: Double): Locationforecast? {
        return datasource.getLocationForecast(lat, lon)
    }
}
