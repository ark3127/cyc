package com.cystem.core.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

/**
 * Type-safe UUID wrapper for all entity identifiers.
 * Serializes as standard UUID string.
 */
@kotlinx.serialization.Serializable(with = Id.Serializer::class)
inline class Id<T>(val value: String) : CharSequence by value {
    companion object {
        fun <T> generate(): Id<T> = Id(UUID.randomUUID().toString())
        fun <T> fromString(string: String): Id<T> = Id(string)
        fun <T> fromUuid(uuid: UUID): Id<T> = Id(uuid.toString())
    }

    override fun toString(): String = value

    class Serializer<T> : KSerializer<Id<T>> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Id", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: Id<T>) { encoder.encodeString(value.value) }
        override fun deserialize(decoder: Decoder): Id<T> = Id(decoder.decodeString())
    }
}
