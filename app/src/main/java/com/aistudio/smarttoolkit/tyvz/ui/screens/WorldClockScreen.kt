package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.WorldClockPurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.WorldClockPurpleBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WorldTimezone(
    val city: String,
    val country: String,
    val zoneId: String,
    val flagEmoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hourOffsetSlider by remember { mutableFloatStateOf(0f) }

    // Live tick every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val timezones = remember {
        listOf(
            WorldTimezone("London", "United Kingdom", "Europe/London", "🇬🇧"),
            WorldTimezone("New York", "USA (EST/EDT)", "America/New_York", "🇺🇸"),
            WorldTimezone("San Francisco", "USA (PST/PDT)", "America/Los_Angeles", "🇺🇸"),
            WorldTimezone("UTC", "Coordinated Universal", "UTC", "🌐"),
            WorldTimezone("New Delhi", "India (IST)", "Asia/Kolkata", "🇮🇳"),
            WorldTimezone("Tokyo", "Japan (JST)", "Asia/Tokyo", "🇯🇵"),
            WorldTimezone("Sydney", "Australia (AEST)", "Australia/Sydney", "🇦🇺"),
            WorldTimezone("Dubai", "UAE (GST)", "Asia/Dubai", "🇦🇪"),
            WorldTimezone("Berlin / Paris", "Europe (CET)", "Europe/Berlin", "🇪🇺")
        )
    }

    val baseTime = currentTimeMs + (hourOffsetSlider * 3600_000).toLong()

    val localZone = remember { TimeZone.getDefault() }
    val localTimeFormat = remember {
        SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).apply {
            timeZone = localZone
        }
    }
    val localDateFormat = remember {
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).apply {
            timeZone = localZone
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "World Clock & Timezones",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (hourOffsetSlider != 0f) {
                        IconButton(onClick = {
                            HapticUtils.performClick(context)
                            hourOffsetSlider = 0f
                        }) {
                            Text("Live", fontWeight = FontWeight.Bold, color = WorldClockPurple, fontSize = 13.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("world_clock_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Local Time Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = WorldClockPurpleBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR LOCAL TIME (${localZone.id})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WorldClockPurple,
                            letterSpacing = 1.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(WorldClockPurple.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Current Location",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WorldClockPurple
                            )
                        }
                    }

                    Text(
                        text = localTimeFormat.format(Date(baseTime)),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4C1D95)
                    )

                    Text(
                        text = localDateFormat.format(Date(baseTime)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5B21B6)
                    )
                }
            }

            // Interactive Time Slider
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TIME COMPARISON SLIDER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (hourOffsetSlider == 0f) "Real-time" else "${if (hourOffsetSlider > 0) "+" else ""}${hourOffsetSlider.toInt()} Hours",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WorldClockPurple
                        )
                    }

                    Slider(
                        value = hourOffsetSlider,
                        onValueChange = {
                            hourOffsetSlider = it
                        },
                        valueRange = -12f..12f,
                        steps = 23,
                        colors = SliderDefaults.colors(
                            thumbColor = WorldClockPurple,
                            activeTrackColor = WorldClockPurple
                        )
                    )
                }
            }

            // Global Cities List
            Text(
                text = "GLOBAL TIME ZONES (${timezones.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            timezones.forEach { tz ->
                val tzObj = TimeZone.getTimeZone(tz.zoneId)
                val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply { timeZone = tzObj }
                val dateFmt = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).apply { timeZone = tzObj }
                val hour24Fmt = SimpleDateFormat("HH", Locale.getDefault()).apply { timeZone = tzObj }

                val formattedTime = timeFmt.format(Date(baseTime))
                val formattedDate = dateFmt.format(Date(baseTime))
                val hour24 = hour24Fmt.format(Date(baseTime)).toIntOrNull() ?: 12
                val isDaytime = hour24 in 6..18

                // Difference calculation
                val diffHours = (tzObj.getOffset(baseTime) - localZone.getOffset(baseTime)) / 3600000.0
                val diffSign = if (diffHours >= 0) "+" else ""
                val diffText = if (diffHours == 0.0) "Same time" else "$diffSign${diffHours.toInt()}h relative"

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tz.flagEmoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(tz.city, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(tz.country, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(diffText, fontSize = 11.sp, color = WorldClockPurple, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isDaytime) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                                    contentDescription = null,
                                    tint = if (isDaytime) Color(0xFFF59E0B) else Color(0xFF6366F1),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = formattedTime,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = formattedDate,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
