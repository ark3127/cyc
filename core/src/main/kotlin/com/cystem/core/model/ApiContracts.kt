package com.cystem.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// ========================================================================
// NVIDIA NIM API CONTRACTS (OpenAI Compatible SSE)
// ========================================================================

@Serializable
data class NimChatRequest(
    val model: String,
    val messages: List<NimMessage>,
    val stream: Boolean = true,
    val temperature: Float? = null,
    val top_p: Float? = null,
    val max_tokens: Int? = null,
    val seed: Long? = null,
    val tools: List<NimTool>? = null,
    val tool_choice: Any? = null, // "auto" | "none" | { "type": "function", "function": {"name": "..."} }
    val reasoning_effort: String? = null // "low" | "medium" | "high"
)

@Serializable
data class NimMessage(
    val role: String, // "system" | "user" | "assistant" | "tool"
    val content: Any? = null, // String or List<NimContentPart>
    val tool_calls: List<NimToolCall>? = null,
    val tool_call_id: String? = null,
    val name: String? = null
)

@Serializable
sealed interface NimContentPart {
    @Serializable data class Text(val type: String = "text", val text: String) : NimContentPart
    @Serializable data class ImageUrl(val type: String = "image_url", val image_url: ImageUrlData) : NimContentPart
}

@Serializable
data class ImageUrlData(val url: String, val detail: String? = "auto")

@Serializable
data class NimTool(val type: String = "function", val function: NimFunction)
@Serializable
data class NimFunction(val name: String, val description: String, val parameters: JsonElement) // JSON Schema

@Serializable
data class NimToolCall(val id: String, val type: String = "function", val function: NimFunctionCall)
@Serializable
data class NimFunctionCall(val name: String, val arguments: String) // JSON String

// --- NIM SSE Streaming Chunks ---
@Serializable
data class NimStreamChunk(
    val id: String,
    val `object`: String = "chat.completion.chunk",
    val created: Long,
    val model: String,
    val choices: List<NimChoice>,
    val usage: NimUsage? = null
)

@Serializable
data class NimChoice(
    val index: Int,
    val delta: NimDelta,
    val finish_reason: String? = null
)

@Serializable
data class NimDelta(
    val role: String? = null,
    val content: String? = null,
    val reasoning_content: String? = null, // Nemotron specific reasoning field
    val tool_calls: List<NimDeltaToolCall>? = null
)

@Serializable
data class NimDeltaToolCall(
    val index: Int,
    val id: String? = null,
    val type: String = "function",
    val function: NimDeltaFunction? = null
)

@Serializable
data class NimDeltaFunction(
    val name: String? = null,
    val arguments: String? = null // Accumulated delta string
)

@Serializable
data class NimUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// ========================================================================
// GOOGLE GEMINI 3.0 API CONTRACTS (v1beta)
// ========================================================================

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val tools: List<GeminiTool>? = null,
    val toolConfig: GeminiToolConfig? = null,
    val safetySettings: List<GeminiSafetySetting>? = null,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@Serializable
data class GeminiContent(
    val role: String, // "user" | "model" | "tool" | "system"
    val parts: List<GeminiPart>
)

@Serializable
sealed interface GeminiPart {
    @Serializable data class Text(val text: String) : GeminiPart
    @Serializable data class InlineData(val inline_data: Blob) : GeminiPart
    @Serializable data class FunctionCall(val function_call: FunctionCallData) : GeminiPart
    @Serializable data class FunctionResponse(val function_response: FunctionResponseData) : GeminiPart
    @Serializable data class FileData(val file_data: FileData) : GeminiPart
}

@Serializable
data class Blob(val mime_type: String, val data: String) // Base64

@Serializable
data class FunctionCallData(val name: String, val args: JsonElement)

@Serializable
data class FunctionResponseData(val name: String, val response: JsonElement)

@Serializable
data class FileData(val mime_type: String, val file_uri: String)

@Serializable
data class GeminiTool(
    val google_search: GoogleSearchTool? = null, // New simplified tool
    val code_execution: CodeExecutionTool? = null,
    val function_declarations: List<GeminiFunctionDeclaration>? = null // Custom tools
)

@Serializable
data class GoogleSearchTool() // Empty object enables grounding. No dynamic config in 3.0.

@Serializable
data class CodeExecutionTool() // Empty object enables code execution.

@Serializable
data class GeminiFunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: JsonElement? = null // JSON Schema
)

@Serializable
data class GeminiToolConfig(
    val function_calling_config: FunctionCallingConfig? = null
)

@Serializable
data class FunctionCallingConfig(
    val mode: String = "AUTO", // AUTO, ANY, NONE
    val allowed_function_names: List<String>? = null
)

@Serializable
data class GeminiSafetySetting(
    val category: String, // HARM_CATEGORY_HATE_SPEECH, etc.
    val threshold: String = "BLOCK_ONLY_HIGH" // BLOCK_NONE, BLOCK_LOW_AND_ABOVE, etc.
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val maxOutputTokens: Int? = null,
    val candidateCount: Int = 1,
    val stopSequences: List<String>? = null,
    val responseMimeType: String? = null, // "application/json" for structured output
    val responseSchema: JsonElement? = null
)

// --- Gemini Response (Streaming & Non-Streaming) ---

@Serializable
data class GeminiStreamResponse(
    val candidates: List<GeminiCandidate>? = null,
    val usageMetadata: GeminiUsageMetadata? = null,
    val modelVersion: String? = null,
    val responseId: String? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String? = null, // STOP, MAX_TOKENS, SAFETY, RECITATION, TOOL_USE
    val index: Int = 0,
    val safetyRatings: List<GeminiSafetyRating>? = null,
    val citationMetadata: GeminiCitationMetadata? = null, // Grounding sources
    val groundingMetadata: GeminiGroundingMetadata? = null, // New in 2.0+/3.0
    val tokenCount: Int? = null
)

@Serializable
data class GeminiGroundingMetadata(
    val webSearchQueries: List<String>? = null,
    val searchEntryPoint: GeminiSearchEntryPoint? = null,
    val groundingChunks: List<GeminiGroundingChunk>? = null,
    val groundingSupports: List<GeminiGroundingSupport>? = null
)

@Serializable
data class GeminiSearchEntryPoint(
    val renderedContent: String? = null // HTML for "Search Results" chip
)

@Serializable
data class GeminiGroundingChunk(
    val web: GeminiWebChunk? = null
)

@Serializable
data class GeminiWebChunk(
    val uri: String,
    val title: String
)

@Serializable
data class GeminiGroundingSupport(
    val segment: GeminiSegment? = null,
    val groundingChunkIndices: List<Int>? = null
)

@Serializable
data class GeminiSegment(
    val startIndex: Int? = null,
    val endIndex: Int? = null,
    val text: String? = null
)

@Serializable
data class GeminiCitationMetadata(
    val citations: List<GeminiCitation>? = null
)

@Serializable
data class GeminiCitation(
    val startIndex: Int? = null,
    val endIndex: Int? = null,
    val uri: String? = null,
    val title: String? = null
)

@Serializable
data class GeminiSafetyRating(
    val category: String,
    val probability: String,
    val blocked: Boolean
)

@Serializable
data class GeminiUsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int,
    val cachedContentTokenCount: Int = 0,
    val toolUsePromptTokenCount: Int = 0
)

// ========================================================================
// INTERNAL DOMAIN EVENTS (UI State)
// ========================================================================

sealed interface GenerationEvent {
    data class TextDelta(val text: String) : GenerationEvent
    data class ReasoningDelta(val text: String) : GenerationEvent
    data class ToolCallStart(val call: ToolCall) : GenerationEvent
    data class ToolCallDelta(val callId: String, val argumentsDelta: String) : GenerationEvent
    data class ToolCallEnd(val call: ToolCall) : GenerationEvent
    data class ToolResult(val result: ToolResult) : GenerationEvent
    data class SourcesFound(val sources: List<Source>) : GenerationEvent
    data class UsageUpdate(val usage: Usage) : GenerationEvent
    data class Completed(val responseId: String?, val model: String) : GenerationEvent
    data class Error(val throwable: Throwable, val isRecoverable: Boolean) : GenerationEvent
    data class Cancelled : GenerationEvent
}
