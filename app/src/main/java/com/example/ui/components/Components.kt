package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.WeatherCardData
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCardBorder
import com.example.ui.theme.MyraCardSurface
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraGold
import com.example.ui.theme.MyraPink
import com.example.ui.theme.MyraPurple
import com.example.ui.theme.MyraPurpleLight
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun RobotAvatar(
    size: Dp = 100.dp,
    showGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * 1.25f)
    ) {
        if (showGlow) {
            // Neon cyan halo ring
            Box(
                modifier = Modifier
                    .size(size * 1.15f)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MyraCyan.copy(alpha = 0.45f),
                                MyraPurple.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Avatar Image container
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(MyraCyan, MyraPurpleLight, MyraPink)
                    ),
                    shape = CircleShape
                )
                .background(Color(0xFF0D152A))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.myra_robot_avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = "MYRA AI Robot Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun AudioWaveVisualizer(
    isListening: Boolean,
    amplitude: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    val barCount = 28

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 24.dp)
    ) {
        for (i in 0 until barCount) {
            val phaseOffset = (i * 120) % 1000
            val animatedHeightFraction by infiniteTransition.animateFloat(
                initialValue = 0.15f,
                targetValue = 0.95f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400 + (i % 5) * 120, delayMillis = phaseOffset, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            val baseFactor = if (isListening) {
                ((amplitude * 0.7f) + (animatedHeightFraction * 0.6f)).coerceIn(0.2f, 1f)
            } else {
                0.15f
            }

            val barHeight = (60.dp * baseFactor)

            // Dynamic color gradient across bars: Cyan -> Magenta -> Purple
            val colorFraction = i.toFloat() / barCount.toFloat()
            val barColor = when {
                colorFraction < 0.33f -> MyraCyan
                colorFraction < 0.66f -> MyraPurpleLight
                else -> MyraPink
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun EmbeddedAudioPlayer(
    isPlaying: Boolean,
    progress: Float,
    durationSeconds: Int,
    currentSeconds: Int,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationText = String.format("%02d:%02d", durationSeconds / 60, durationSeconds % 60)
    val currentText = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60)
    val displayTimer = if (isPlaying) currentText else durationText

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F5F9).copy(alpha = 0.95f) // Matching screenshot white/light player bar
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Speaker Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Voice Audio",
                tint = Color(0xFF0284C7),
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
            )

            // Play/Pause button
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("audio_play_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause Audio" else "Play Audio",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(26.dp)
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { if (isPlaying) progress else 0f },
                color = Color(0xFF0284C7),
                trackColor = Color(0xFFCBD5E1),
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Duration text
            Text(
                text = displayTimer,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(end = 6.dp)
            )
        }
    }
}

@Composable
fun WeatherCard(
    weather: WeatherCardData,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7).copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = weather.iconEmoji,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column {
                Text(
                    text = "${weather.maxTemp} / ${weather.minTemp}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = weather.condition,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
            }
        }
    }
}

@Composable
fun TipBanner(
    stepNumber: Int,
    text: String,
    onNextTip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stepBg = when (stepNumber) {
        1 -> Color(0xFF0284C7)
        2 -> Color(0xFF10B981)
        3 -> Color(0xFF8B5CF6)
        4 -> Color(0xFFEC4899)
        5 -> Color(0xFFF97316)
        else -> Color(0xFF0284C7)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(stepBg.copy(alpha = 0.95f))
            .clickable { onNextTip() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Text(
                    text = stepNumber.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = stepBg
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                lineHeight = 17.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GoogleSearchGroundingCard(
    queries: List<com.example.data.model.SearchSource>,
    searchQueries: List<String>,
    modifier: Modifier = Modifier
) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1B35)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .border(1.dp, MyraCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Google Search Header Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🌐",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Google Search द्वारा सत्यापित (Search Grounded)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyraCyan
                )
            }

            // Search queries used
            if (searchQueries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "सर्च: ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyraTextSecondary
                    )
                    Text(
                        text = searchQueries.joinToString(", ") { "\"$it\"" },
                        fontSize = 10.sp,
                        color = Color(0xFFBAE6FD)
                    )
                }
            }

            // Sources / Citations
            if (queries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "स्रोत व संदर्भ (Sources):",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyraTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    queries.take(3).forEach { source ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF16254A))
                                .clickable {
                                    try {
                                        uriHandler.openUri(source.url)
                                    } catch (_: Exception) {}
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔗",
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = source.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "देखें ↗",
                                fontSize = 9.sp,
                                color = MyraCyan
                            )
                        }
                    }
                }
            }
        }
    }
}
