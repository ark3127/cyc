package com.cystem.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    // API Keys are NOT stored here. They live in SecureStorage (Keystore).
    // These fields are null placeholders for DataStore migration clarity only.
    
    // Global Model Config
    val defaultMainModel: String = ModelRegistry.DEFAULT_MAIN_MODEL,
    val defaultReasoningEffort: ReasoningEffort = ReasoningEffort.Medium,
    val defaultTemperature: Float = 0.6f,
    val defaultTopP: Float = 0.95f,
    val defaultMaxTokens: Int = 8192,

    // Behavior
    val autoSearchEnabled: Boolean = true,
    val streamingEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val biometricLockEnabled: Boolean = false,
    val reducedMotion: Boolean = false,

    // UI / Theme
    val themeMode: ThemeMode = ThemeMode.System,
    val accentColor: Int = 0xFF00CCAA,
    val fontScale: Float = 1.0f,

    // Tools
    val enabledPhoneTools: Set<String> = setOf(),
    val memoryEnabled: Boolean = true,
    val memoryEntries: List<MemoryEntry> = emptyList()
)

@Serializable
enum class ThemeMode { System, Light, Dark }

@Serializable
data class MemoryEntry(
    val id: Id<MemoryEntry>,
    val key: String,
    val value: String,
    val isEnabled: Boolean = true,
    val createdAt: String
)
