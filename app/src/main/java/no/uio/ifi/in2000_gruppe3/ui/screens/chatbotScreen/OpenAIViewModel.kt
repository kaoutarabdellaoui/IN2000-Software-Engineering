package no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.repository.HikeAPIRepository
import no.uio.ifi.in2000_gruppe3.data.openAIAPI.repository.OpenAIRepository
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

class OpenAIViewModel: ViewModel() {
    private val openAIRepository = OpenAIRepository()
    private val hikeAPIRepository = HikeAPIRepository(this)

    private val _openAIUIState = MutableStateFlow(OpenAIUIState())
    val openAIUIState: StateFlow<OpenAIUIState> = _openAIUIState

    private val _conversationHistory = mutableStateListOf<ChatbotMessage>()
    val conversationHistory: List<ChatbotMessage> get() = _conversationHistory

    init {
        if (_conversationHistory.isEmpty()) {
            addBotMessage("Hei! Jeg er turbotten Ånund 🤖. Hva kan jeg hjelpe deg med?")
        }
    }

    fun addUserMessage(message: String) {
        _conversationHistory.add(ChatbotMessage(message, true))
    }

    fun addBotMessage(message: String) {
        _conversationHistory.add(ChatbotMessage(message, false))
    }

    // Returns a full response from the OpenAI API
    fun getCompletionsSamples(prompt: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _openAIUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val response = openAIRepository.getCompletionsSamples(prompt)
                val text = response.choices?.first()?.message?.content ?: "No response received"

                _openAIUIState.update {
                    it.copy(response = text)
                }

                onResult(text)
            } catch (e: Exception) {
                val error = "Nå har du trykket så mye at serveren er lei av deg. Gå deg en tur!"
                _openAIUIState.update {
                    it.copy(response = error)
                }

                onResult(error)
                Log.d("OpenAIViewModel", "getCompletionsSamples: ${e.message}")
            } finally {
                _openAIUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    // Returns a streaming response from the OpenAI API
    fun getCompletionsStream(prompt: String) {
        if (_openAIUIState.value.isStreaming) {
            return
        }
        viewModelScope.launch {
            _openAIUIState.update {
                it.copy(isLoading = true, response = "")
            }

            try {
                val botMessage = ChatbotMessage("", false)
                _conversationHistory.add(botMessage)
                val messageIndex = _conversationHistory.size - 1

                // Collect the streaming responses
                openAIRepository.getCompletionsStream(prompt).collectLatest { chunk ->
                    _openAIUIState.update {
                        // Append each new chunk to the existing response
                        it.copy(
                            response = it.response + chunk,
                            isLoading = false,
                            isStreaming = true
                        )
                    }
                    _conversationHistory[messageIndex] =
                        _conversationHistory[messageIndex].copy(
                            content = _conversationHistory[messageIndex].content + chunk
                        )
                }
            } catch (e: Exception) {
                _openAIUIState.update {
                    it.copy(
                        response = "Nå har du trykket så mye at serveren er lei av deg. Gå deg en tur!",
                        isLoading = false,
                        isStreaming = false
                    )
                }
                Log.d("OpenAIViewModel", "getCompletionsStream: ${e.message}")
            } finally {
                if (openAIUIState.value.response.contains("€")) {
                    addFeature()
                }
                _openAIUIState.update {
                    it.copy(isLoading = false, isStreaming = false)
                }
            }
        }
    }

    fun addFeature() {
        val responseData = openAIUIState.value.response.split("€")
        val textResponse = responseData.first()
        val coordinates = responseData.last().trim()
        val latLng = coordinates.split(",")
        val lat = latLng[0].toDouble()
        val lng = latLng[1].toDouble()

        viewModelScope.launch {
            val features = hikeAPIRepository.getHikes(lat, lng, 1, "Fotrute", 500)
            val chatBotMessage = ChatbotMessage(
                content = textResponse,
                isFromUser = false,
                feature = features.first()
            )
            _conversationHistory[_conversationHistory.size - 1] = chatBotMessage
        }
    }

    fun getChatbotResponse(
        input: String,
        homeScreenViewModel: HomeScreenViewModel
    ) {
        var prompt = "Du er turbotten Ånund og er en turguide i en turapp. " +
                "Meldingen nederst er sendt til deg fra en bruker av appen. " +
                "Du skal kun svare på spørsmålet fra brukeren uten å gi noen annen informasjon. " +
                "Du skal ikke gi noen annen informasjon enn det som er nødvendig for å svare på spørsmålet. " +
                "Du skal kun svare på spørsmål som er relatert til turer, friluftsliv og været. " +
                "Hvis spørsmålet ikke er relatert til det så skal du gi en melding hvor du forteller hvem du er som sier at du kun svarer på spørsmål som er relevante. " +
                "Svar på en hyggelig måte. " +
                "Her er chat historikken som hva vi har snakket om tidligere: $conversationHistory. " +
                "Her er meldingen fra bruker: $input. " +
                "Du har kun tilgang tl turer i Oslo og Akershus. " +
                "Du vet ingenting om lengden på turen, hvor lang tid det tar å gå turen eller hvor vanskelig turen er. " +
                "Du vet heller ikke nøyaktig posisjon til turen, bare omtrent hvor den er." +
                "Hvis du i denne meldingen skriver om en spesifikk tur så avslutt meldingen med å bruke tegnet \"€\" og legg til nøyaktige koordinater til turen etter tegnet. " +
                "Send koordinatene som lat, lng uten noe annet tekst eller symboler. " +
                "Du skal ikke nevne koordinatene før du avslutter meldingen med \"€\". "

        if (input.contains("vær") || input.contains("temperatur")) {
            prompt += "Her er informasjonene du trenger om været: ${homeScreenViewModel.homeScreenUIState.value.forecast?.properties?.timeseries}"
        }

        getCompletionsStream(prompt)
    }

    // Used to get recommended hikes from AI, shown in bottom sheet
    suspend fun getRecommendedHikes(
        homeScreenViewModel: HomeScreenViewModel,
        hikeScreenViewModel: HikeScreenViewModel
    ) {
        // Clear previous hikes to show loader if needed
        hikeScreenViewModel.updateRecommendedHikes(emptyList())

        val prompt = "Basert på værforholdet jeg sender til slutt, " +
                "gi meg tre forslag til turer hvor det er bra vær i oslo og akershus. " +
                "Ikke gi meg turer som er rett ved siden av hverandre. " +
                "Du skal kun sende koordinatene til de tre turene, ikke noe annet. " +
                "Ikke bruk noen andre tegn. " +
                "Send koordinatene som: lat lng,lat lng,lat lng" +
                "Hvis værdata ikke er tilgjengelig så sender du bare tilfeldige koordianter i oslo og akershus. " +
                "All informasjon du trenger om værforholdet finner du her: ${homeScreenViewModel.homeScreenUIState.value.forecast}"

        getCompletionsStream(prompt)

        // Wait to response is updated
        while (_openAIUIState.value.isStreaming || _openAIUIState.value.isLoading) {
            delay(100)
        }

        val hikes = mutableListOf<Feature>()
        val response = _openAIUIState.value.response

        if (response.isNotEmpty()) {
            val hikesCoords = response.split(",")
            hikesCoords.forEach { coords ->
                try {
                    val latLng = coords.split(" ")
                    if (latLng.size == 2) {
                        val lat = latLng.first().toDouble()
                        val lng = latLng.last().toDouble()

                        val hike = hikeAPIRepository.getHikes(
                            lat,
                            lng,
                            1,
                            "Fotrute",
                            500
                        )

                        if (hike.isNotEmpty()) {
                            hikes.add(hike.first())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("OpenAIViewModel", "getRecommendedHikes: Error parsing coordinates", e)
                }
            }
        } else {
            Log.e("OpenAIViewModel", "getRecommendedHikes: No response received")
        }

        hikeScreenViewModel.updateRecommendedHikes(hikes)
        hikeScreenViewModel.updateRecommendedHikesLoaded(true)
    }
}

data class OpenAIUIState(
    val response: String = "",
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false
)
