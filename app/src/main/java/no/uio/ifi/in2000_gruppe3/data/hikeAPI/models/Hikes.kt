package no.uio.ifi.in2000_gruppe3.data.hikeAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Hikes(
    val crs: Crs,
    val features: List<Feature>,
    val name: String,
    val type: String
)