package com.cystem.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.compositeEncoder

@Serializable
data class Message(
    val id: Id<Message>,
    val conversationId: Id<Conversation>,
    val role: Role,
    val content: List<ContentPart>,
    val timestamp: Instant,
    val status: Status = Status.Sending,
    val modelUsed: String? = null,
    val toolCalls: List<ToolCall> = emptyList(),
    val toolResults: List<ToolResult> = emptyList(),
    val reasoning: String? = null,
    val sources: List<Source> = emptyList(),
    val usage: Usage? = null,
    val responseId: String? = null,
    val error: String? = null
) {
    companion object {
        fun user(conversationId: Id<Conversation>, text: String): Message = Message(
            id = Id.generate(),
            conversationId = conversationId,
            role = Role.User,
            content = listOf(ContentPart.Text(text)),
            timestamp = Instant.now()
        )

        fun assistant(conversationId: Id<Conversation>, modelUsed: String): Message = Message(
            id = Id.generate(),
            conversationId = conversationId,
            role = Role.Assistant,
            content = emptyList(),
            timestamp = Instant.now(),
            status = Status.Streaming,
            modelUsed = modelUsed
        )

        fun system(conversationId: Id<Conversation>, text: String): Message = Message(
            id = Id.generate(),
            conversationId = conversationId,
            role = Role.System,
            content = listOf(ContentPart.Text(text)),
            timestamp = Instant.now()
        )
    }

    val textContent: String
        get() = content.filterIsInstance<ContentPart.Text>().joinToString("") { it.text }

    val images: List<ContentPart.Image>
        get() = content.filterIsInstance<ContentPart.Image>()
}

@Serializable
enum class Role { System, User, Assistant, Tool }

@Serializable
enum class Status { Sending, Streaming, Done, Error, Cancelled }

@Serializable
data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val cachedTokens: Int = 0, // Gemini 3.0 supports context caching
    val reasoningTokens: Int = 0
)

@Serializable
data class Source(
    val title: String,
    val uri: String,
    val snippet: String? = null,
    val date: String? = null
)

@Serializable
data class ToolCall(
    val id: String,
    val name: String,
    val arguments: String
)

@Serializable
data class ToolResult(
    val callId: String,
    val name: String,
    val result: String,
    val isError: Boolean = false
)

// --- Polymorphic Content Parts ---

@Serializable
sealed interface ContentPart {
    @Serializable
    data class Text(val text: String) : ContentPart

    @Serializable
    data class Image(
        val uri: String,
        val mimeType: String,
        val width: Int? = null,
        val height: Int? = null,
        val altText: String? = null
    ) : ContentPart

    @Serializable
    data class ToolResultRef(val callId: String, val name: String) : ContentPart

    @Serializable
    data class Reasoning(val text: String) : ContentPart
}
