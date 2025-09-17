package no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Locationforecast(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)