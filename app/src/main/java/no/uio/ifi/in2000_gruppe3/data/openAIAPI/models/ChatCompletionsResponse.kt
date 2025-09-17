package no.uio.ifi.in2000_gruppe3.data.openAIAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionsResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
)