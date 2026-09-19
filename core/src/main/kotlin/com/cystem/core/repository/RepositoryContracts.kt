package com.cystem.core.repository

import com.cystem.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Pure domain contracts. Zero Android dependencies.
 * Implemented in :data module.
 */

// --- Conversation Repository ---
interface ConversationRepository {
    suspend fun create(conversation: Conversation): Id<Conversation>
    suspend fun update(conversation: Conversation)
    suspend fun delete(id: Id<Conversation>)
    suspend fun deleteAll()
    suspend fun getById(id: Id<Conversation>): Conversation?
    
    // Paging Source for UI lists (sorted by updatedAt desc, pinned first)
    fun observeAll(): Flow<PagingSource<Int, Conversation>>
    
    // Search
    suspend fun search(query: String): List<Conversation>
    
    // Pinning
    suspend fun togglePin(id: Id<Conversation>)
    
    // Title Generation
    suspend fun updateTitle(id: Id<Conversation>, title: String)
}

// --- Message Repository ---
interface MessageRepository {
    suspend fun insert(message: Message)
    suspend fun insertAll(messages: List<Message>)
    suspend fun update(message: Message) // For streaming updates (content, status, reasoning)
    suspend fun updateStatus(id: Id<Message>, status: Status, error: String? = null)
    suspend fun appendContent(id: Id<Message>, part: ContentPart) // Streaming optimization
    suspend fun appendReasoning(id: Id<Message>, delta: String)
    suspend fun setToolCalls(id: Id<Message>, calls: List<ToolCall>)
    suspend fun addToolResult(id: Id<Message>, result: ToolResult)
    suspend fun finalizeMessage(id: Id<Message>, usage: Usage?, responseId: String?, sources: List<Source>)
    suspend fun delete(id: Id<Message>)
    suspend fun deleteByConversation(conversationId: Id<Conversation>)
    suspend fun deleteAll()
    
    // Observation
    fun observeByConversation(conversationId: Id<Conversation>): Flow<PagingSource<Int, Message>>
    suspend fun getLatestUserMessage(conversationId: Id<Conversation>): Message?
    suspend fun getMessageCount(conversationId: Id<Conversation>): Int
}

// --- Attachment Repository ---
interface AttachmentRepository {
    suspend fun insert(attachment: Attachment)
    suspend fun updateAnalysis(id: Id<Attachment>, analysis: AttachmentAnalysis)
    suspend fun delete(id: Id<Attachment>)
    suspend fun deleteByMessage(messageId: Id<Message>)
    fun observeByMessage(messageId: Id<Message>): Flow<List<Attachment>>
    suspend fun getById(id: Id<Attachment>): Attachment?
}

// --- Settings Repository ---
interface SettingsRepository {
    // DataStore backed
    val settingsFlow: Flow<AppSettings>
    suspend fun updateSettings(block: AppSettings.() -> Unit)
    
    // Secure Storage backed (Keystore)
    suspend fun saveApiKey(provider: ApiProvider, key: String)
    suspend fun getApiKey(provider: ApiProvider): String?
    suspend fun deleteApiKey(provider: ApiProvider)
    suspend fun hasApiKey(provider: ApiProvider): Boolean
    
    // Biometric
    suspend fun setBiometricEnabled(enabled: Boolean)
    val isBiometricEnabledFlow: Flow<Boolean>
}

enum class ApiProvider { NVIDIA, GEMINI }

// --- Memory Repository ---
interface MemoryRepository {
    val memoryFlow: Flow<List<MemoryEntry>>
    suspend fun upsert(entry: MemoryEntry)
    suspend fun delete(id: Id<MemoryEntry>)
    suspend fun clear()
    suspend fun setEnabled(enabled: Boolean)
}

// --- Export/Import ---
interface BackupRepository {
    suspend fun exportBackup(): Result<ByteArray>
    suspend fun importBackup(data: ByteArray): Result<Unit>
}

// --- Paging Abstraction (Pure Kotlin) ---
interface PagingSource<Key : Any, Value : Any> {
    suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value>
    fun getRefreshKey(state: PagingState<Key, Value>): Key?
}

data class LoadParams<Key>(val key: Key?, val loadSize: Int)
sealed interface LoadResult<Key, Value> {
    data class Page<Key, Value>(val data: List<Value>, val prevKey: Key?, val nextKey: Key?) : LoadResult<Key, Value>
    data class Error<Key, Value>(val error: Throwable) : LoadResult<Key, Value>
}
data class PagingState<Key, Value>(val pages: List<LoadResult.Page<Key, Value>>, val anchorPosition: Int)
