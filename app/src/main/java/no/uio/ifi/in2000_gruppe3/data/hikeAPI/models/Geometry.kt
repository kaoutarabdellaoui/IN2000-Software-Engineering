package no.uio.ifi.in2000_gruppe3.data.hikeAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val coordinates: List<List<Double>>,
    val type: String
)