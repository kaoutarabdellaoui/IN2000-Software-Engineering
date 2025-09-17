package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Geometry {
    abstract val type: String

    @Serializable
    @SerialName("Polygon")
    data class Polygon(
        override val type: String,
        val coordinates: List<List<List<Double>>>
    ) : Geometry()

    @Serializable
    @SerialName("MultiPolygon")
    data class MultiPolygon(
        override val type: String,
        val coordinates: List<List<List<List<Double>>>>
    ) : Geometry()
}