package no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Next6Hours(
    val details: DetailsX?,
    val summary: Summary
)