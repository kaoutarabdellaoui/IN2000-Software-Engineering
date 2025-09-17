package no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val units: Units,
    val updated_at: String
)