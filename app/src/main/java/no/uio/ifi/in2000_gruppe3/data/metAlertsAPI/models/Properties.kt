package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    val altitude_above_sea_level: Int? = null,
    val area: String? = null,
    val awarenessResponse: String? = null,
    val awarenessSeriousness: String? = null,
    val awareness_level: String? = null,
    val awareness_type: String? = null,
    val ceiling_above_sea_level: Int,
    val certainty: String? = null,
    val consequences: String? = null,
    val contact: String? = null,
    val county: List<String>? = null,
    val description: String? = null,
    val event: String? = null,
    val eventAwarenessName: String? = null,
    val eventEndingTime: String? = null,
    val geographicDomain: String? = null,
    val id: String? = null,
    val instruction: String? = null,
    val municipality: List<String>? = null,
    val resources: List<Resource>? = null,
    val riskMatrixColor: String? = null,
    val severity: String? = null,
    val status: String? = null,
    val title: String? = null,
    val triggerLevel: String? = null,
    val type: String? = null,
    val web: String? = null
)