package no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    val meta: Meta,
    val timeseries: List<TimeSeries>
)