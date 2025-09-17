package no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen

import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature

data class ChatbotMessage(
    val content: String,
    val isFromUser: Boolean,
    val feature: Feature? = null
)