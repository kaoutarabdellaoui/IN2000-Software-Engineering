package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class MetAlerts(
    val features: List<Feature>,
    val lang: String,
    val lastChange: String,
    val type: String
)