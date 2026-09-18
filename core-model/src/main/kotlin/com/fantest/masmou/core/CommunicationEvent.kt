package com.fantest.masmou.core

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

enum class EventType {
    YES, NO, WATER, PAIN, TOILET, NURSE, FAMILY, URGENT, TEXT
}

object MasmouBle {
    const val SERVICE_UUID = "72b91f40-7c20-4a77-a1bc-97f1dc746b55"
    const val EVENT_CHARACTERISTIC_UUID = "45db4440-c29b-43a7-8e35-f6bb1e629fea"
}

data class CommunicationEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: EventType,
    val timestamp: Long = System.currentTimeMillis(),
    val text: String? = null,
    val urgency: Int = 0,
    val protocolVersion: Int = 1,
)

object EventProtocol {
    fun encode(event: CommunicationEvent): String {
        val encodedText = URLEncoder.encode(event.text.orEmpty(), StandardCharsets.UTF_8.name())
        return listOf(
            event.protocolVersion,
            event.id,
            event.type.name,
            event.timestamp,
            event.urgency,
            encodedText,
        ).joinToString("|")
    }

    fun decode(raw: String): CommunicationEvent? {
        val parts = raw.split("|", limit = 6)
        if (parts.size != 6) return null
        val version = parts[0].toIntOrNull() ?: return null
        if (version != 1) return null
        val type = runCatching { EventType.valueOf(parts[2]) }.getOrNull() ?: return null
        val timestamp = parts[3].toLongOrNull() ?: return null
        val urgency = parts[4].toIntOrNull() ?: return null
        val text = URLDecoder.decode(parts[5], StandardCharsets.UTF_8.name()).ifBlank { null }
        return CommunicationEvent(
            id = parts[1],
            type = type,
            timestamp = timestamp,
            text = text,
            urgency = urgency,
            protocolVersion = version,
        )
    }
}

fun hasRepeatedPain(
    events: List<CommunicationEvent>,
    now: Long = System.currentTimeMillis(),
): Boolean {
    val cutoff = now - 60L * 60L * 1000L
    return events.count { it.type == EventType.PAIN && it.timestamp in cutoff..now } >= 2
}
