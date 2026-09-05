package com.fantest.masmou.patient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val MasmouNavy = Color(0xFF15324B)
private val MasmouTeal = Color(0xFF2F6F62)
private val MasmouBackground = Color(0xFFF3F6F8)
private val MasmouOutline = Color(0xFF6A7C89)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
        )
        setContent { MasmouPatientApp() }
    }
}

@Composable
private fun MasmouPatientApp() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MasmouNavy,
            secondary = MasmouTeal,
            background = MasmouBackground,
            surface = Color.White,
            onBackground = MasmouNavy,
            onSurface = MasmouNavy,
            outline = MasmouOutline,
            secondaryContainer = Color(0xFFDDEDE9),
            onSecondaryContainer = MasmouNavy,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            PatientShell()
        }
    }
}

@Composable
private fun PatientShell() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        val compact = maxHeight < 420.dp || maxWidth < 760.dp
        val outerPadding = if (compact) 10.dp else 20.dp
        val spacing = if (compact) 8.dp else 16.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            PatientHeader(compact = compact)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing),
            ) {
                CommunicationArea(
                    modifier = Modifier
                        .weight(1.65f)
                        .fillMaxHeight(),
                    compact = compact,
                )
                QuickActionArea(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    compact = compact,
                    spacing = spacing,
                )
            }

            BottomToolArea(compact = compact, spacing = spacing)
        }
    }
}

@Composable
private fun PatientHeader(compact: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 54.dp else 68.dp),
        shape = RoundedCornerShape(if (compact) 16.dp else 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 14.dp else 20.dp,
                vertical = if (compact) 8.dp else 12.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier.semantics { heading() },
                    style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (!compact) {
                    Text(
                        text = stringResource(R.string.welcome_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = stringResource(R.string.status_offline_ready),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = if (compact) 7.dp else 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun CommunicationArea(modifier: Modifier, compact: Boolean) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 18.dp else 24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
        tonalElevation = 1.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compact) 12.dp else 22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.communication_area_title),
                    modifier = Modifier.semantics { heading() },
                    style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.communication_area_hint),
                    style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun QuickActionArea(
    modifier: Modifier,
    compact: Boolean,
    spacing: Dp,
) {
    val actions = listOf(
        R.string.quick_action_one,
        R.string.quick_action_two,
        R.string.quick_action_three,
        R.string.quick_action_four,
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 18.dp else 24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)),
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 8.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            Text(
                text = stringResource(R.string.quick_actions_title),
                modifier = Modifier.semantics { heading() },
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing),
            ) {
                actions.chunked(2).forEach { rowActions ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        rowActions.forEach { labelRes ->
                            PlaceholderButton(
                                label = stringResource(labelRes),
                                minHeight = if (compact) 48.dp else 64.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderButton(
    label: String,
    modifier: Modifier = Modifier,
    minHeight: Dp = 64.dp,
) {
    OutlinedButton(
        onClick = {},
        modifier = modifier.heightIn(min = minHeight),
        enabled = false,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BottomToolArea(compact: Boolean, spacing: Dp) {
    val tools = listOf(R.string.tool_one, R.string.tool_two, R.string.tool_three)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 60.dp else 78.dp),
        shape = RoundedCornerShape(if (compact) 16.dp else 20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)),
    ) {
        Row(
            modifier = Modifier.padding(if (compact) 6.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            Text(
                text = stringResource(R.string.tools_title),
                modifier = Modifier.padding(horizontal = if (compact) 4.dp else 8.dp),
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            tools.forEach { labelRes ->
                PlaceholderButton(
                    label = stringResource(labelRes),
                    minHeight = if (compact) 48.dp else 56.dp,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
