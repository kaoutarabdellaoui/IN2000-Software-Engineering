package no.uio.ifi.in2000_gruppe3.data.openAIAPI.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000_gruppe3.BuildConfig
import no.uio.ifi.in2000_gruppe3.data.openAIAPI.models.ChatCompletionsRequest
import no.uio.ifi.in2000_gruppe3.data.openAIAPI.models.ChatCompletionsResponse
import no.uio.ifi.in2000_gruppe3.data.openAIAPI.models.ChatMessage

class OpenAIDatasource {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json { ignoreUnknownKeys = true }
            )
        }
        expectSuccess = true
    }

    private val endpoint = "https://uio-mn-ifi-in2000-swe1.openai.azure.com/"
    private val apiKey = BuildConfig.OPENAI_API_KEY_1
    private val modelName = "gpt-4o"
    private val apiVersion = "2023-05-15"

    // Returns a full response from the OpenAI API
    suspend fun getCompletionsSamples(prompt: String): ChatCompletionsResponse = withContext(Dispatchers.IO) {
        val chatRequest = ChatCompletionsRequest(
            messages = listOf(
                ChatMessage(
                    role = "user",
                    content = prompt
                )
            )
        )

        val completionsUrl = "$endpoint/openai/deployments/$modelName/chat/completions?api-version=$apiVersion"

        client.post(completionsUrl) {
            contentType(ContentType.Application.Json)
            header("api-key", apiKey)
            setBody(chatRequest)
        }.body()
    }

    // Returns a streaming response from the OpenAI API
    fun getCompletionsStream(prompt: String): Flow<String> = flow {
        val chatRequest = ChatCompletionsRequest(
            messages = listOf(
                ChatMessage(
                    role = "user",
                    content = prompt
                )
            ),
            stream = true
        )

        val completionsUrl = "$endpoint/openai/deployments/$modelName/chat/completions?api-version=$apiVersion"

        client.preparePost(completionsUrl) {
            contentType(ContentType.Application.Json)
            header("api-key", apiKey)
            setBody(chatRequest)
        }.execute { response ->
            val channel = response.bodyAsChannel()

            val lineBuffer = StringBuilder()

            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (true) {
                bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                if (bytesRead <= 0) break

                // Convert bytes to string with UTF-8 encoding
                val chunk = buffer.decodeToString(0, bytesRead)
                lineBuffer.append(chunk)

                // Process complete lines from the buffer
                var endLineIndex = lineBuffer.indexOf("\n")
                while (endLineIndex >= 0) {
                    val line = lineBuffer.substring(0, endLineIndex)
                    lineBuffer.delete(0, endLineIndex + 1) // Remove processed line

                    // Process the complete line
                    if (line.startsWith("data: ") && line != "data: [DONE]") {
                        processJsonLine(line.substring(6))?.let { content ->
                            emit(content)
                        }
                    }

                    // Look for next line
                    endLineIndex = lineBuffer.indexOf("\n")
                }
            }

            // Process any remaining content in the buffer
            if (lineBuffer.isNotEmpty()) {
                val line = lineBuffer.toString()
                if (line.startsWith("data: ") && line != "data: [DONE]") {
                    processJsonLine(line.substring(6))?.let { content ->
                        emit(content)
                    }
                }
            }
        }
    }

    // Helper function to process a JSON line and extract the content
    private fun processJsonLine(jsonData: String): String? {
        return try {
            val jsonElement = Json.parseToJsonElement(jsonData.trim())
            val choices = jsonElement.jsonObject["choices"]?.jsonArray

            if (choices != null && choices.isNotEmpty()) {
                val delta = choices[0].jsonObject["delta"]?.jsonObject
                delta?.get("content")?.jsonPrimitive?.content
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error parsing JSON: ${e.message}")
            null
        }
    }
}
