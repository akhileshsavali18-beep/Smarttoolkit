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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.HeartRateRed
import com.aistudio.smarttoolkit.tyvz.ui.theme.HeartRateRedBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import kotlin.math.roundToInt

data class HrZone(
    val name: String,
    val intensity: String,
    val minPct: Double,
    val maxPct: Double,
    val color: Color,
    val benefit: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateZonesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var ageInput by remember { mutableStateOf("28") }
    var rhrInput by remember { mutableStateOf("65") } // Resting heart rate

    val age = ageInput.toIntOrNull()?.coerceIn(10, 100) ?: 28
    val rhr = rhrInput.toIntOrNull()?.coerceIn(40, 120) ?: 65

    // Standard formula: 220 - age
    val maxHr = 220 - age
    // Tanaka formula: 208 - 0.7 * age
    val tanakaMaxHr = (208 - 0.7 * age).roundToInt()

    val zones = remember {
        listOf(
            HrZone("Zone 1: Warm Up & Recovery", "50% - 60%", 0.50, 0.60, Color(0xFF10B981), "Improves overall health & promotes recovery"),
            HrZone("Zone 2: Fat Burn & Base", "60% - 70%", 0.60, 0.70, Color(0xFF0284C7), "Builds aerobic base & burns fat efficiently"),
            HrZone("Zone 3: Aerobic Fitness", "70% - 80%", 0.70, 0.80, Color(0xFFF59E0B), "Strengthens cardiovascular stamina"),
            HrZone("Zone 4: Anaerobic Threshold", "80% - 90%", 0.80, 0.90, Color(0xFFEA580C), "Increases high-speed endurance & VO2"),
            HrZone("Zone 5: Maximum Performance", "90% - 100%", 0.90, 1.00, Color(0xFFDC2626), "Peak sprint capacity & neuromotor power")
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Target Heart Rate Zones",
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
                    IconButton(onClick = {
                        HapticUtils.performClick(context)
                        ageInput = "25"
                        rhrInput = "65"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("heart_rate_zones_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Fields Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "AGE & RESTING PARAMETERS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    AppInputField(
                        value = ageInput,
                        onValueChange = { ageInput = it },
                        label = "Your Age (Years)",
                        suffixText = "years",
                        placeholder = "e.g. 28",
                        testTag = "input_age"
                    )

                    AppInputField(
                        value = rhrInput,
                        onValueChange = { rhrInput = it },
                        label = "Resting Heart Rate (optional)",
                        suffixText = "bpm",
                        placeholder = "e.g. 65",
                        testTag = "input_rhr"
                    )
                }
            }

            // Max HR Banner Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HeartRateRedBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = HeartRateRed, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "ESTIMATED MAX HEART RATE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeartRateRed,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "$maxHr BPM",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "Tanaka scientific formula: $tanakaMaxHr BPM",
                            fontSize = 12.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }

                    Button(
                        onClick = {
                            val text = "Target HR Zones for Age $age:\nMax HR: $maxHr BPM\n" +
                                    zones.joinToString("\n") {
                                        "${it.name}: ${(maxHr * it.minPct).roundToInt()} - ${(maxHr * it.maxPct).roundToInt()} BPM"
                                    }
                            copyToClipboard(context, "Heart Rate Zones", text)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartRateRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Zones List
            Text(
                text = "TRAINING ZONES BREAKDOWN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            zones.forEach { zone ->
                val minBpm = (maxHr * zone.minPct).roundToInt()
                val maxBpm = (maxHr * zone.maxPct).roundToInt()

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(zone.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(zone.color.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = zone.intensity,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = zone.color
                                )
                            }
                        }

                        Text(
                            text = "$minBpm – $maxBpm BPM",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = zone.color
                        )

                        Text(
                            text = zone.benefit,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
