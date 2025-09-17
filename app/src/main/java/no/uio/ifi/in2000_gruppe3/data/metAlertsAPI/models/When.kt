package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class When(
    val interval: List<String>
)