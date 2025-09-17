package no.uio.ifi.in2000_gruppe3.data.openAIAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionsRequest(
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 800,
    val stream: Boolean = false
)