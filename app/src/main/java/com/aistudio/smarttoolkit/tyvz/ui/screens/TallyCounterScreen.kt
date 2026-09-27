package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.theme.TallyPurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.TallyPurpleBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

data class TallyLap(val lapNumber: Int, val count: Int, val timeStamp: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TallyCounterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var count by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(33) } // Popular default target
    var vibrationEnabled by remember { mutableStateOf(true) }
    var showResetDialog by remember { mutableStateOf(false) }
    val laps = remember { mutableStateListOf<TallyLap>() }

    fun increment(amount: Int = 1) {
        if (vibrationEnabled) {
            HapticUtils.performClick(context)
        }
        val newCount = (count + amount).coerceAtLeast(0)
        if (newCount == targetCount && targetCount > 0) {
            HapticUtils.performSuccess(context)
        }
        count = newCount
    }

    fun decrement(amount: Int = 1) {
        if (vibrationEnabled) {
            HapticUtils.performClick(context)
        }
        count = (count - amount).coerceAtLeast(0)
    }

    val progress = if (targetCount > 0) {
        (count.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200),
        label = "tally_progress"
    )

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Counter?") },
            text = { Text("This will reset the current count ($count) back to 0. Recorded laps will remain.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticUtils.performClick(context)
                        count = 0
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Digital Tally Counter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("tally_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Vibration Toggle
                    IconButton(
                        onClick = {
                            vibrationEnabled = !vibrationEnabled
                            if (vibrationEnabled) HapticUtils.performClick(context)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Toggle Vibration",
                            tint = if (vibrationEnabled) TallyPurple else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Reset action
                    IconButton(
                        onClick = {
                            if (count > 0) showResetDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Counter",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target selector presets
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                listOf(33, 100, 500, 1000).forEach { preset ->
                    val isSelected = targetCount == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            HapticUtils.performClick(context)
                            targetCount = preset
                        },
                        label = { Text("$preset", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TallyPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Big Giant Counter Interactive Area
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                TallyPurple.copy(alpha = 0.15f),
                                TallyPurpleBg.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        increment(1)
                    }
                    .testTag("tally_tap_area")
            ) {
                // Circular Progress Indicator
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(250.dp),
                    color = TallyPurple,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = count,
                        transitionSpec = {
                            (scaleIn(initialScale = 0.85f) + fadeIn()).togetherWith(
                                scaleOut(targetScale = 1.15f) + fadeOut()
                            )
                        },
                        label = "counter_text"
                    ) { targetNum ->
                        Text(
                            text = "$targetNum",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            color = TallyPurple
                        )
                    }

                    if (targetCount > 0) {
                        Text(
                            text = "Goal: $targetCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (targetCount > 0 && count >= targetCount) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Target reached",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Target Reached!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    } else {
                        Text(
                            text = "TAP ANYWHERE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Quick Step Controls (+1, +5, +10, -1)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Decrement (-1)
                IconButton(
                    onClick = { decrement(1) },
                    enabled = count > 0,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("tally_minus_1")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Minus 1",
                        tint = if (count > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }

                // Primary Increment (+1)
                Button(
                    onClick = { increment(1) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TallyPurple),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("tally_plus_1")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add 1", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Step +5
                OutlinedButton(
                    onClick = { increment(5) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("tally_plus_5")
                ) {
                    Text("+5", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Step +10
                OutlinedButton(
                    onClick = { increment(10) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("tally_plus_10")
                ) {
                    Text("+10", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Lap / Record Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (count > 0) {
                            HapticUtils.performSuccess(context)
                            val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                            laps.add(0, TallyLap(laps.size + 1, count, sdf.format(java.util.Date())))
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Record Lap",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (laps.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { laps.clear() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear Laps", fontSize = 12.sp)
                    }
                }
            }

            // Lap List
            if (laps.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Recorded Laps (${laps.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            itemsIndexed(laps) { _, lap ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Lap #${lap.lapNumber} • ${lap.timeStamp}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${lap.count}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TallyPurple
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
