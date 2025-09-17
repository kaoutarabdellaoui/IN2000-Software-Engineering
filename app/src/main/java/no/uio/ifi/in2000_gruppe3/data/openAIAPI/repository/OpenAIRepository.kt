package no.uio.ifi.in2000_gruppe3.data.openAIAPI.repository

import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000_gruppe3.data.openAIAPI.datasource.OpenAIDatasource
import no.uio.ifi.in2000_gruppe3.data.openAIAPI.models.ChatCompletionsResponse

class OpenAIRepository {
    private val openAIDatasource = OpenAIDatasource()

    suspend fun getCompletionsSamples(prompt: String): ChatCompletionsResponse {
        return openAIDatasource.getCompletionsSamples(prompt)
    }

    fun getCompletionsStream(prompt: String): Flow<String> {
        return openAIDatasource.getCompletionsStream(prompt)
    }
}