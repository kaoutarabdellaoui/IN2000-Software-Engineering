package no.uio.ifi.in2000_gruppe3.data.openAIAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)