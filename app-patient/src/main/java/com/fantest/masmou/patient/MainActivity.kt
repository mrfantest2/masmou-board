package com.fantest.masmou.patient

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantest.masmou.core.CommunicationEvent
import com.fantest.masmou.core.EventProtocol
import com.fantest.masmou.core.EventType
import java.util.Locale

private val MasmouNavy = Color(0xFF15324B)
private val MasmouTeal = Color(0xFF2F6F62)
private val MasmouBackground = Color(0xFFF3F6F8)
private val MasmouRed = Color(0xFFC63D3D)
private val MasmouBlue = Color(0xFF1F5FAE)
private val MasmouGreen = Color(0xFF2F7D4A)

private data class DrawStroke(
    val points: SnapshotStateList<Offset>,
    val color: Color,
    val width: Float,
)

class MainActivity : ComponentActivity() {
    private lateinit var bleServer: BlePatientServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleServer = BlePatientServer(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
        )
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (!isEmulator()) requestBlePermissionsIfNeeded()
        setContent { MasmouPatientApp() }
    }

    override fun onResume() {
        super.onResume()
        if (!isEmulator()) bleServer.start()
    }

    override fun onDestroy() {
        bleServer.stop()
        super.onDestroy()
    }

    fun publishEvent(type: EventType, text: String, urgency: Int = 0) {
        val event = CommunicationEvent(
            type = type,
            text = text,
            urgency = urgency,
        )
        bleServer.publish(event)
        if (isEmulator()) {
            sendBroadcast(
                Intent("com.fantest.masmou.DEBUG_EVENT")
                    .setPackage("com.fantest.masmou.family")
                    .putExtra("payload", EventProtocol.encode(event)),
            )
        }
    }

    private fun isEmulator(): Boolean =
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
            Build.MODEL.contains("sdk_gphone", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true)

    private fun requestBlePermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
            val missing = permissions.filter { checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED }
            if (missing.isNotEmpty()) {
                requestPermissions(missing.toTypedArray(), 4101)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 4101 && grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
            bleServer.start()
        }
    }
}

@Composable
private fun MasmouPatientApp() {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val haptics = LocalHapticFeedback.current
    var arabic by rememberSaveable { mutableStateOf(false) }
    var lowVision by rememberSaveable { mutableStateOf(false) }
    var typedText by rememberSaveable { mutableStateOf("") }
    var lastMessage by rememberSaveable { mutableStateOf("") }
    var speechRate by rememberSaveable { mutableFloatStateOf(0.9f) }
    var ttsReady by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            tts = null
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return
        val locale = if (arabic) Locale.forLanguageTag("ar") else Locale.ENGLISH
        tts?.language = locale
        tts?.setSpeechRate(speechRate)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "masmou-" + System.nanoTime())
        val type = when (text) {
            "Yes", "نعم" -> EventType.YES
            "No", "لا" -> EventType.NO
            "Water", "أريد ماء" -> EventType.WATER
            "Pain", "لدي ألم" -> EventType.PAIN
            "Toilet", "أحتاج الحمام" -> EventType.TOILET
            "Nurse / Help", "أحتاج الممرضة" -> EventType.NURSE
            "Family", "أريد عائلتي" -> EventType.FAMILY
            "Urgent", "الأمر عاجل" -> EventType.URGENT
            else -> EventType.TEXT
        }
        val urgency = if (type == EventType.PAIN || type == EventType.URGENT) 2 else 0
        activity?.publishEvent(type, text, urgency)
        lastMessage = text
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MasmouNavy,
            secondary = MasmouTeal,
            background = MasmouBackground,
            surface = Color.White,
            onBackground = MasmouNavy,
            onSurface = MasmouNavy,
            error = MasmouRed,
        ),
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (arabic) LayoutDirection.Rtl else LayoutDirection.Ltr,
        ) {
            PatientScreen(
                arabic = arabic,
                onArabicChange = { arabic = it },
                lowVision = lowVision,
                onLowVisionChange = { lowVision = it },
                typedText = typedText,
                onTypedTextChange = { typedText = it },
                lastMessage = lastMessage,
                onSpeak = ::speak,
                ttsReady = ttsReady,
                speechRate = speechRate,
                onSpeechRateChange = { speechRate = it },
            )
        }
    }
}

@Composable
private fun PatientScreen(
    arabic: Boolean,
    onArabicChange: (Boolean) -> Unit,
    lowVision: Boolean,
    onLowVisionChange: (Boolean) -> Unit,
    typedText: String,
    onTypedTextChange: (String) -> Unit,
    lastMessage: String,
    onSpeak: (String) -> Unit,
    ttsReady: Boolean,
    speechRate: Float,
    onSpeechRateChange: (Float) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        val compact = maxHeight < 430.dp || maxWidth < 780.dp
        val gap = if (compact) 8.dp else 14.dp
        Column(
            modifier = Modifier.fillMaxSize().padding(if (compact) 8.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            HeaderBar(arabic, onArabicChange, lowVision, onLowVisionChange, ttsReady, compact)
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                WritingPanel(
                    modifier = Modifier.weight(1.55f).fillMaxHeight(),
                    arabic = arabic,
                    lowVision = lowVision,
                    compact = compact,
                )
                QuickActionsPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    arabic = arabic,
                    lowVision = lowVision,
                    compact = compact,
                    onSpeak = onSpeak,
                )
            }
            TypedSpeechBar(
                arabic = arabic,
                lowVision = lowVision,
                compact = compact,
                typedText = typedText,
                onTypedTextChange = onTypedTextChange,
                onSpeak = onSpeak,
                lastMessage = lastMessage,
                speechRate = speechRate,
                onSpeechRateChange = onSpeechRateChange,
            )
        }
    }
}

@Composable
private fun HeaderBar(
    arabic: Boolean,
    onArabicChange: (Boolean) -> Unit,
    lowVision: Boolean,
    onLowVisionChange: (Boolean) -> Unit,
    ttsReady: Boolean,
    compact: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().heightIn(min = if (compact) 54.dp else 66.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (arabic) "مسموع" else "Masmou Patient",
                    modifier = Modifier.semantics { heading() },
                    fontWeight = FontWeight.Bold,
                    fontSize = if (lowVision) 28.sp else if (compact) 20.sp else 24.sp,
                    color = MasmouNavy,
                )
                if (!compact) {
                    Text(
                        text = if (arabic) "مساحة تواصل بسيطة وآمنة" else "Simple, dignified communication",
                        fontSize = if (lowVision) 18.sp else 14.sp,
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = if (ttsReady) Color(0xFFDDEDE9) else Color(0xFFFFE9C7),
            ) {
                Text(
                    text = if (arabic) {
                        if (ttsReady) "جاهز بدون إنترنت" else "الصوت غير جاهز"
                    } else {
                        if (ttsReady) "Offline ready" else "Speech unavailable"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    fontWeight = FontWeight.SemiBold,
                )
            }
            FilterChip(
                selected = lowVision,
                onClick = { onLowVisionChange(!lowVision) },
                label = { Text(if (arabic) "رؤية أوضح" else "Low vision") },
            )
            Button(
                onClick = { onArabicChange(!arabic) },
                colors = ButtonDefaults.buttonColors(containerColor = MasmouNavy),
            ) {
                Text(if (arabic) "EN" else "عربي")
            }
        }
    }
}

@Composable
private fun WritingPanel(
    modifier: Modifier,
    arabic: Boolean,
    lowVision: Boolean,
    compact: Boolean,
) {
    val strokes = remember { mutableStateListOf<DrawStroke>() }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var eraser by remember { mutableStateOf(false) }
    var locked by rememberSaveable { mutableStateOf(false) }
    var clearArmed by remember { mutableStateOf(false) }
    var redrawTick by remember { mutableIntStateOf(0) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(2.dp, MasmouNavy.copy(alpha = 0.18f)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(if (compact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (arabic) "اكتب أو ارسم" else "Write or draw",
                    modifier = Modifier.weight(1f).semantics { heading() },
                    fontWeight = FontWeight.Bold,
                    fontSize = if (lowVision) 24.sp else 18.sp,
                )
                ColorChoice(Color.Black, selectedColor, eraser) { selectedColor = Color.Black; eraser = false }
                ColorChoice(MasmouBlue, selectedColor, eraser) { selectedColor = MasmouBlue; eraser = false }
                ColorChoice(MasmouRed, selectedColor, eraser) { selectedColor = MasmouRed; eraser = false }
                ColorChoice(MasmouGreen, selectedColor, eraser) { selectedColor = MasmouGreen; eraser = false }
                FilterChip(
                    selected = eraser,
                    onClick = { eraser = !eraser },
                    label = { Text(if (arabic) "ممحاة" else "Eraser") },
                )
                OutlinedButton(onClick = { locked = !locked }) {
                    Text(
                        if (locked) {
                            if (arabic) "فتح" else "Unlock"
                        } else {
                            if (arabic) "قفل" else "Lock"
                        },
                    )
                }
                OutlinedButton(
                    onClick = {
                        if (!locked) {
                            if (clearArmed) {
                                strokes.clear()
                                redrawTick++
                                clearArmed = false
                            } else {
                                clearArmed = true
                            }
                        }
                    },
                    enabled = !locked,
                ) {
                    Text(
                        if (clearArmed) {
                            if (arabic) "تأكيد المسح" else "Confirm clear"
                        } else {
                            if (arabic) "مسح" else "Clear"
                        },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, MasmouNavy.copy(alpha = 0.16f), RoundedCornerShape(16.dp)),
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(locked, selectedColor, eraser, lowVision) {
                            if (!locked) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        clearArmed = false
                                        strokes.add(
                                            DrawStroke(
                                                points = mutableStateListOf(offset),
                                                color = if (eraser) Color.White else selectedColor,
                                                width = if (eraser) 34f else if (lowVision) 11f else 7f,
                                            ),
                                        )
                                        redrawTick++
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        strokes.lastOrNull()?.points?.add(change.position)
                                        redrawTick++
                                    },
                                )
                            }
                        }
                        .semantics {
                            contentDescription = if (arabic) "مساحة الرسم والكتابة" else "Drawing and writing canvas"
                        },
                ) {
                    redrawTick
                    strokes.forEach { stroke ->
                        if (stroke.points.size == 1) {
                            drawCircle(stroke.color, stroke.width / 2f, stroke.points.first())
                        } else {
                            stroke.points.zipWithNext().forEach { (a, b) ->
                                drawLine(
                                    color = stroke.color,
                                    start = a,
                                    end = b,
                                    strokeWidth = stroke.width,
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                    }
                }
                if (strokes.isEmpty()) {
                    Text(
                        text = if (locked) {
                            if (arabic) "اللوحة مقفلة" else "Canvas locked"
                        } else {
                            if (arabic) "استخدم إصبعك أو القلم" else "Use your finger or stylus"
                        },
                        modifier = Modifier.align(Alignment.Center),
                        color = MasmouNavy.copy(alpha = 0.45f),
                        fontSize = if (lowVision) 24.sp else 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorChoice(
    color: Color,
    selectedColor: Color,
    eraser: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(12.dp),
        border = BorderStroke(if (!eraser && selectedColor == color) 4.dp else 2.dp, MasmouNavy.copy(alpha = 0.55f)),
    ) {
        Box(modifier = Modifier.background(color, CircleShape).padding(9.dp))
    }
}

private data class QuickAction(val en: String, val ar: String, val urgent: Boolean = false)

@Composable
private fun QuickActionsPanel(
    modifier: Modifier,
    arabic: Boolean,
    lowVision: Boolean,
    compact: Boolean,
    onSpeak: (String) -> Unit,
) {
    val actions = remember {
        listOf(
            QuickAction("Yes", "نعم"),
            QuickAction("No", "لا"),
            QuickAction("Water", "أريد ماء"),
            QuickAction("Pain", "لدي ألم", true),
            QuickAction("Toilet", "أحتاج الحمام"),
            QuickAction("Nurse / Help", "أحتاج الممرضة"),
            QuickAction("Family", "أريد عائلتي"),
            QuickAction("Urgent", "الأمر عاجل", true),
        )
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MasmouNavy.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(if (compact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 9.dp),
        ) {
            Text(
                text = if (arabic) "احتياجات سريعة" else "Quick needs",
                modifier = Modifier.semantics { heading() },
                fontWeight = FontWeight.Bold,
                fontSize = if (lowVision) 24.sp else 18.sp,
            )
            actions.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 9.dp),
                ) {
                    row.forEach { action ->
                        Button(
                            onClick = { onSpeak(if (arabic) action.ar else action.en) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (action.urgent) Color(0xFFFDE2E2) else Color(0xFFEAF0F4),
                                contentColor = if (action.urgent) MasmouRed else MasmouNavy,
                            ),
                        ) {
                            Text(
                                text = if (arabic) action.ar else action.en,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (lowVision) 22.sp else if (compact) 15.sp else 18.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypedSpeechBar(
    arabic: Boolean,
    lowVision: Boolean,
    compact: Boolean,
    typedText: String,
    onTypedTextChange: (String) -> Unit,
    onSpeak: (String) -> Unit,
    lastMessage: String,
    speechRate: Float,
    onSpeechRateChange: (Float) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MasmouNavy.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(if (compact) 7.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = typedText,
                onValueChange = onTypedTextChange,
                modifier = Modifier.weight(1.4f),
                label = { Text(if (arabic) "اكتب رسالة" else "Type a message") },
                singleLine = true,
                textStyle = TextStyle(fontSize = if (lowVision) 22.sp else 17.sp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
            Button(
                onClick = { onSpeak(typedText) },
                enabled = typedText.isNotBlank(),
                modifier = Modifier.heightIn(min = if (lowVision) 60.dp else 52.dp),
            ) {
                Text(if (arabic) "تحدث" else "Speak", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { onTypedTextChange("") },
                modifier = Modifier.heightIn(min = if (lowVision) 60.dp else 52.dp),
            ) {
                Text(if (arabic) "حذف" else "Clear text")
            }
            Column(modifier = Modifier.weight(0.7f)) {
                Text(
                    text = (if (arabic) "سرعة الصوت " else "Speech ") + (speechRate * 100).toInt() + "%",
                    fontSize = if (lowVision) 16.sp else 12.sp,
                )
                Slider(
                    value = speechRate,
                    onValueChange = onSpeechRateChange,
                    valueRange = 0.5f..1.3f,
                )
            }
            if (!compact && lastMessage.isNotBlank()) {
                Text(
                    text = (if (arabic) "آخر رسالة: " else "Last: ") + lastMessage,
                    modifier = Modifier.weight(0.9f),
                    maxLines = 2,
                    fontSize = if (lowVision) 16.sp else 12.sp,
                    color = MasmouNavy.copy(alpha = 0.72f),
                )
            }
        }
    }
}
