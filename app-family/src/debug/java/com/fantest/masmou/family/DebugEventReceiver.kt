package com.fantest.masmou.family

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.fantest.masmou.core.EventProtocol
import com.fantest.masmou.core.hasRepeatedPain

class DebugEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!isEmulator()) return
        val payload = intent.getStringExtra("payload") ?: return
        val event = EventProtocol.decode(payload) ?: return
        val store = FamilyEventStore(context)
        store.insert(event)
        val repeatedPain = hasRepeatedPain(store.recent())
        store.close()
        if (repeatedPain) showNotification(context)
    }

    private fun isEmulator(): Boolean =
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
            Build.MODEL.contains("sdk_gphone", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true)

    private fun showNotification(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "masmou_alerts",
                    "Masmou alerts",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Important communication alerts from Masmou Patient"
                },
            )
        }
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "masmou_alerts")
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }
        builder
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Masmou: repeated pain")
            .setContentText("Two pain requests were received within 60 minutes.")
            .setAutoCancel(true)

        manager.notify(7001, builder.build())
    }
}
