package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String,
    val `when`: When
)