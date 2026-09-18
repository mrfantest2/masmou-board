package com.fantest.masmou.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CommunicationEventTest {
    @Test
    fun protocolRoundTripPreservesEvent() {
        val source = CommunicationEvent(
            id = "event-1",
            type = EventType.TEXT,
            timestamp = 123456789L,
            text = "مرحبا | hello",
            urgency = 1,
        )
        val decoded = EventProtocol.decode(EventProtocol.encode(source))
        assertNotNull(decoded)
        assertEquals(source, decoded)
    }

    @Test
    fun malformedMessageIsRejected() {
        assertEquals(null, EventProtocol.decode("bad"))
        assertEquals(null, EventProtocol.decode("9|x|PAIN|1|1|text"))
    }

    @Test
    fun repeatedPainRequiresTwoEventsWithinOneHour() {
        val now = 2_000_000L
        val recent = CommunicationEvent(type = EventType.PAIN, timestamp = now - 10_000L)
        val older = CommunicationEvent(type = EventType.PAIN, timestamp = now - 3_700_000L)
        assertFalse(hasRepeatedPain(listOf(recent, older), now))
        assertTrue(hasRepeatedPain(listOf(recent, recent.copy(id = "second")), now))
    }
}
