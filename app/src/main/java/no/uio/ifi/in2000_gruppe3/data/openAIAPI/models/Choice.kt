package no.uio.ifi.in2000_gruppe3.data.openAIAPI.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Choice(
    val index: Int? = null,
    val message: ChatMessage? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)