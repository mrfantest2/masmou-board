package com.fantest.masmou.family

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantest.masmou.core.CommunicationEvent
import com.fantest.masmou.core.EventType
import com.fantest.masmou.core.hasRepeatedPain
import java.text.DateFormat
import java.util.Date

private val Navy = Color(0xFF15324B)
private val Teal = Color(0xFF2F6F62)
private val Bg = Color(0xFFF3F6F8)
private val AlertRed = Color(0xFFC63D3D)

class MainActivity : ComponentActivity() {
    private lateinit var store: FamilyEventStore
    private lateinit var client: BleFamilyClient

    private var connectionState by mutableStateOf("Starting")
    private var events by mutableStateOf<List<CommunicationEvent>>(emptyList())
    private var painAlert by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
        )

        store = FamilyEventStore(this)
        events = store.recent()
        painAlert = hasRepeatedPain(events)

        client = BleFamilyClient(
            context = this,
            onState = { state -> runOnUiThread { connectionState = state } },
            onEvent = { event -> runOnUiThread { acceptEvent(event) } },
        )
        createNotificationChannel()
        if (isEmulator()) {
            connectionState = "Emulator link"
        } else {
            requestPermissionsIfNeeded()
        }

        setContent {
            FamilyApp(
                connectionState = connectionState,
                events = events,
                painAlert = painAlert,
                onConnect = { client.start() },
                onClear = {
                    store.clear()
                    events = emptyList()
                    painAlert = false
                },
            )
        }
    }

    override fun onResume() {
        super.onResume()
        events = store.recent()
        painAlert = hasRepeatedPain(events)
        if (!isEmulator()) client.start()
    }

    override fun onDestroy() {
        if (!isEmulator()) client.stop()
        store.close()
        super.onDestroy()
    }

    private fun isEmulator(): Boolean =
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
            Build.MODEL.contains("sdk_gphone", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true)

    private fun acceptEvent(event: CommunicationEvent) {
        store.insert(event)
        events = store.recent()
        val newPainAlert = hasRepeatedPain(events)
        if (newPainAlert && !painAlert) showPainNotification()
        painAlert = newPainAlert
    }

    private fun requestPermissionsIfNeeded() {
        val missing = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                missing += Manifest.permission.BLUETOOTH_SCAN
            }
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                missing += Manifest.permission.BLUETOOTH_CONNECT
            }
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                missing += Manifest.permission.ACCESS_FINE_LOCATION
            }
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            missing += Manifest.permission.POST_NOTIFICATIONS
        }
        if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), 4201)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 4201) client.start()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(
                    "masmou_alerts",
                    "Masmou alerts",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Important communication alerts from Masmou Patient"
                },
            )
        }
    }

    private fun showPainNotification() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "masmou_alerts")
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        builder
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Masmou: repeated pain")
            .setContentText("Two pain requests were received within 60 minutes.")
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)

        getSystemService(NotificationManager::class.java).notify(7001, builder.build())
    }
}

@Composable
private fun FamilyApp(
    connectionState: String,
    events: List<CommunicationEvent>,
    painAlert: Boolean,
    onConnect: () -> Unit,
    onClear: () -> Unit,
) {
    var arabic by rememberSaveable { mutableStateOf(false) }
    val scheme = lightColorScheme(
        primary = Navy,
        secondary = Teal,
        background = Bg,
        surface = Color.White,
        onBackground = Navy,
        onSurface = Navy,
        error = AlertRed,
    )

    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (arabic) LayoutDirection.Rtl else LayoutDirection.Ltr,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Bg)
                    .safeDrawingPadding()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (arabic) "مسموع للعائلة" else "Masmou Family",
                            modifier = Modifier.semantics { heading() },
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Navy,
                        )
                        Text(
                            text = if (arabic) "متابعة التواصل محلياً عبر البلوتوث" else "Local Bluetooth communication companion",
                            fontSize = 14.sp,
                            color = Navy.copy(alpha = 0.7f),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (connectionState == "Connected") Color(0xFFDDEDE9) else Color(0xFFFFE9C7),
                    ) {
                        Text(
                            text = connectionState,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Button(onClick = { arabic = !arabic }) {
                        Text(if (arabic) "EN" else "عربي")
                    }
                }

                if (painAlert) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFFE5E5),
                        border = BorderStroke(2.dp, AlertRed),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (arabic) "تنبيه ألم متكرر" else "Repeated pain alert",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = AlertRed,
                            )
                            Text(
                                text = if (arabic) {
                                    "تم استلام طلبَي ألم خلال 60 دقيقة. هذا تنبيه تواصل وليس تشخيصاً طبياً."
                                } else {
                                    "Two pain requests were received within 60 minutes. This is a communication alert, not a diagnosis."
                                },
                                color = AlertRed,
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onConnect,
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    ) {
                        Text(if (arabic) "اتصال" else "Connect")
                    }
                    OutlinedButton(onClick = onClear) {
                        Text(if (arabic) "مسح السجل" else "Clear history")
                    }
                    Text(
                        text = if (arabic) "آخر " + events.size + " حدث" else events.size.toString() + " recent events",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Text(
                    text = if (arabic) "سجل التواصل" else "Communication history",
                    modifier = Modifier.semantics { heading() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

                if (events.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                    ) {
                        Text(
                            text = if (arabic) "لا توجد رسائل بعد" else "No communication events yet",
                            modifier = Modifier.padding(28.dp),
                            textAlign = TextAlign.Center,
                            color = Navy.copy(alpha = 0.55f),
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(events, key = { it.id }) { event ->
                            EventCard(event = event, arabic = arabic)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: CommunicationEvent, arabic: Boolean) {
    val urgent = event.type == EventType.PAIN || event.type == EventType.URGENT || event.urgency > 0
    val label = when (event.type) {
        EventType.YES -> if (arabic) "نعم" else "Yes"
        EventType.NO -> if (arabic) "لا" else "No"
        EventType.WATER -> if (arabic) "ماء" else "Water"
        EventType.PAIN -> if (arabic) "ألم" else "Pain"
        EventType.TOILET -> if (arabic) "الحمام" else "Toilet"
        EventType.NURSE -> if (arabic) "ممرضة / مساعدة" else "Nurse / Help"
        EventType.FAMILY -> if (arabic) "العائلة" else "Family"
        EventType.URGENT -> if (arabic) "عاجل" else "Urgent"
        EventType.TEXT -> if (arabic) "رسالة" else "Typed message"
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (urgent) Color(0xFFFFF0F0) else Color.White,
        border = BorderStroke(1.dp, if (urgent) AlertRed.copy(alpha = 0.5f) else Navy.copy(alpha = 0.12f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (urgent) AlertRed else Navy,
                )
                if (!event.text.isNullOrBlank()) {
                    Text(text = event.text.orEmpty(), fontSize = 15.sp)
                }
            }
            Text(
                text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(event.timestamp)),
                fontSize = 13.sp,
                color = Navy.copy(alpha = 0.62f),
            )
        }
    }
}
