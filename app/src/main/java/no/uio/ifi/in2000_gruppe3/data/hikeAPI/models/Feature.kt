package no.uio.ifi.in2000_gruppe3.data.hikeAPI.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Feature(
    val geometry: Geometry,
    val properties: PropertiesX,
    val type: String,
    @Transient var color: Color? = Color.Blue,
    @Transient var description: String? = null,
    @Transient var difficultyInfo: DifficultyInfo = DifficultyInfo("UKJENT", Color.Gray)
)