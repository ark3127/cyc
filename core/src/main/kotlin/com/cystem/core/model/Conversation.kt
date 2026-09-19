package com.cystem.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: Id<Conversation>,
    val title: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isPinned: Boolean = false,
    val systemPromptOverride: String? = null,
    val modelId: String = ModelRegistry.DEFAULT_MAIN_MODEL,
    val reasoningEffort: ReasoningEffort = ReasoningEffort.Medium,
    val temperature: Float = 0.6f,
    val topP: Float = 0.95f,
    val maxTokens: Int = 8192, // Increased for Gemini 3.0 large context
    val seed: Long? = null
) {
    companion object {
        fun create(title: String = "New Chat"): Conversation {
            val now = Instant.now()
            return Conversation(
                id = Id.generate(),
                title = title,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}

@Serializable
enum class ReasoningEffort { None, Low, Medium, High }
