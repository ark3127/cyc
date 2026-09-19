package com.cystem.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Id<Attachment>,
    val messageId: Id<Message>?,
    val uri: String,
    val mimeType: String,
    val fileName: String,
    val fileSize: Long,
    val width: Int? = null,
    val height: Int? = null,
    val thumbnailUri: String? = null,
    val analysis: AttachmentAnalysis? = null,
    val createdAt: Instant = Instant.now()
) {
    val isImage: Boolean get() = mimeType.startsWith("image/")
    val isVideo: Boolean get() = mimeType.startsWith("video/")
    val isAudio: Boolean get() = mimeType.startsWith("audio/")
    val isText: Boolean get() = mimeType.startsWith("text/") || mimeType == "application/json" || mimeType == "application/pdf"
}

@Serializable
data class AttachmentAnalysis(
    val modelId: String,
    val summary: String,
    val extractedText: String? = null,
    val objects: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val spatialLayout: String? = null,
    val analyzedAt: Instant = Instant.now()
)
