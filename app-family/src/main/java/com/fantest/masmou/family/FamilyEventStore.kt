package com.fantest.masmou.family

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fantest.masmou.core.CommunicationEvent
import com.fantest.masmou.core.EventType

class FamilyEventStore(context: Context) :
    SQLiteOpenHelper(context, "masmou_family.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE events (
                id TEXT PRIMARY KEY,
                type TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                text TEXT,
                urgency INTEGER NOT NULL,
                protocolVersion INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX idx_events_timestamp ON events(timestamp DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun insert(event: CommunicationEvent) {
        val values = ContentValues().apply {
            put("id", event.id)
            put("type", event.type.name)
            put("timestamp", event.timestamp)
            put("text", event.text)
            put("urgency", event.urgency)
            put("protocolVersion", event.protocolVersion)
        }
        writableDatabase.insertWithOnConflict(
            "events",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE,
        )
        writableDatabase.execSQL(
            """
            DELETE FROM events
            WHERE id NOT IN (
                SELECT id FROM events
                ORDER BY timestamp DESC
                LIMIT 100
            )
            """.trimIndent(),
        )
    }

    fun recent(limit: Int = 100): List<CommunicationEvent> {
        val result = mutableListOf<CommunicationEvent>()
        readableDatabase.query(
            "events",
            arrayOf("id", "type", "timestamp", "text", "urgency", "protocolVersion"),
            null,
            null,
            null,
            null,
            "timestamp DESC",
            limit.toString(),
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val typeIndex = cursor.getColumnIndexOrThrow("type")
            val timestampIndex = cursor.getColumnIndexOrThrow("timestamp")
            val textIndex = cursor.getColumnIndexOrThrow("text")
            val urgencyIndex = cursor.getColumnIndexOrThrow("urgency")
            val versionIndex = cursor.getColumnIndexOrThrow("protocolVersion")
            while (cursor.moveToNext()) {
                val type = runCatching { EventType.valueOf(cursor.getString(typeIndex)) }.getOrNull() ?: continue
                result += CommunicationEvent(
                    id = cursor.getString(idIndex),
                    type = type,
                    timestamp = cursor.getLong(timestampIndex),
                    text = cursor.getString(textIndex),
                    urgency = cursor.getInt(urgencyIndex),
                    protocolVersion = cursor.getInt(versionIndex),
                )
            }
        }
        return result
    }

    fun clear() {
        writableDatabase.delete("events", null, null)
    }
}
