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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.SleepCyclePurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.SleepCyclePurpleBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

private fun formatTime(hour24: Int, minute: Int): String {
    val h12 = if (hour24 % 12 == 0) 12 else hour24 % 12
    val ampm = if (hour24 < 12) "AM" else "PM"
    return String.format("%d:%02d %s", h12, minute, ampm)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepCycleCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mode by remember { mutableIntStateOf(0) } // 0: Wake up at..., 1: Sleep at...
    var selectedHour by remember { mutableIntStateOf(7) } // 0-23
    var selectedMinute by remember { mutableIntStateOf(0) } // 0, 15, 30, 45

    // Average 14 minutes to fall asleep
    val fallAsleepMinutes = 14
    val cycleMinutes = 90

    // Compute times for 3, 4, 5, 6 cycles
    val cyclesList = remember(mode, selectedHour, selectedMinute) {
        val baseTotalMinutes = selectedHour * 60 + selectedMinute
        (3..6).map { cycles ->
            val totalSleepTime = cycles * cycleMinutes
            val targetMinutes = if (mode == 0) {
                // To wake up at X, bedtime is X - sleep - fallAsleep
                (baseTotalMinutes - totalSleepTime - fallAsleepMinutes + 24 * 60 * 2) % (24 * 60)
            } else {
                // If sleep at X, wakeup is X + fallAsleep + sleep
                (baseTotalMinutes + fallAsleepMinutes + totalSleepTime) % (24 * 60)
            }
            val hour = targetMinutes / 60
            val minute = targetMinutes % 60
            val hoursOfSleep = (cycles * 90) / 60.0
            Triple(cycles, formatTime(hour, minute), hoursOfSleep)
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_sleep_cycle"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sleep Cycle Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("sleep_cycle_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            selectedHour = 7
                            selectedMinute = 0
                            mode = 0
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SleepCyclePurpleBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleepCyclePurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "90-Minute REM Cycle Science",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SleepCyclePurple
                        )
                        Text(
                            text = "Wake up energized by timing alarms between sleep cycles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = mode == 0,
                    onClick = {
                        HapticUtils.performClick(context)
                        mode = 0
                    },
                    label = { Text("I want to wake up at...") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = mode == 1,
                    onClick = {
                        HapticUtils.performClick(context)
                        mode = 1
                    },
                    label = { Text("I plan to sleep at...") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Time Selector Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (mode == 0) "Target Wake-Up Time" else "Bedtime",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = formatTime(selectedHour, selectedMinute),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = SleepCyclePurple
                            )
                        )
                    }

                    // Hour slider
                    Text("Hour (${selectedHour}:00)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = selectedHour.toFloat(),
                        onValueChange = { selectedHour = it.toInt() },
                        valueRange = 0f..23f,
                        steps = 22,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Minute chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 15, 30, 45).forEach { m ->
                            FilterChip(
                                selected = selectedMinute == m,
                                onClick = {
                                    HapticUtils.performClick(context)
                                    selectedMinute = m
                                },
                                label = { Text(":$m") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Results List
            Text(
                text = if (mode == 0) "Recommended Bedtimes (to fall asleep around)" else "Recommended Wake-Up Times",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            cyclesList.reversed().forEach { (cycles, time, hours) ->
                val isOptimal = cycles in 5..6
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOptimal) SleepCyclePurpleBg else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOptimal) SleepCyclePurple else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                if (isOptimal) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "★ OPTIMAL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleepCyclePurple
                                    )
                                }
                            }
                            Text(
                                text = "$cycles sleep cycles ($hours hours of rest)",
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
