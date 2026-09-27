package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.BreathingCyan
import com.aistudio.smarttoolkit.tyvz.ui.theme.BreathingCyanBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import kotlinx.coroutines.delay

enum class BreathingPhase(val label: String, val instruction: String) {
    INHALE("INHALE", "Breathe in deeply through nose"),
    HOLD("HOLD", "Hold breath gently"),
    EXHALE("EXHALE", "Slowly exhale completely through mouth")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingExerciseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRunning by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var countdownSeconds by remember { mutableIntStateOf(4) }
    var completedCycles by remember { mutableIntStateOf(0) }
    var isHapticsEnabled by remember { mutableStateOf(true) }

    // 4-7-8 rhythm: Inhale 4s, Hold 7s, Exhale 8s
    val inhaleDuration = 4
    val holdDuration = 7
    val exhaleDuration = 8

    // Active breathing loop
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect

        while (isRunning) {
            // INHALE
            currentPhase = BreathingPhase.INHALE
            if (isHapticsEnabled) HapticUtils.performSuccess(context)
            for (s in inhaleDuration downTo 1) {
                countdownSeconds = s
                delay(1000L)
            }

            // HOLD
            currentPhase = BreathingPhase.HOLD
            if (isHapticsEnabled) HapticUtils.performTick(context)
            for (s in holdDuration downTo 1) {
                countdownSeconds = s
                delay(1000L)
            }

            // EXHALE
            currentPhase = BreathingPhase.EXHALE
            if (isHapticsEnabled) HapticUtils.performTick(context)
            for (s in exhaleDuration downTo 1) {
                countdownSeconds = s
                delay(1000L)
            }

            completedCycles++
        }
    }

    val targetCircleSize = when (currentPhase) {
        BreathingPhase.INHALE -> 230.dp
        BreathingPhase.HOLD -> 210.dp
        BreathingPhase.EXHALE -> 130.dp
    }

    val animatedSize by animateDpAsState(
        targetValue = if (isRunning) targetCircleSize else 160.dp,
        animationSpec = tween(
            durationMillis = when (currentPhase) {
                BreathingPhase.INHALE -> inhaleDuration * 1000
                BreathingPhase.HOLD -> 1000
                BreathingPhase.EXHALE -> exhaleDuration * 1000
            }
        ),
        label = "circleSize"
    )

    val targetColor = when (currentPhase) {
        BreathingPhase.INHALE -> Color(0xFF0284C7)
        BreathingPhase.HOLD -> Color(0xFF7C3AED)
        BreathingPhase.EXHALE -> Color(0xFF0D9488)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(800),
        label = "circleColor"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Breathing Exercise",
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
                        isHapticsEnabled = !isHapticsEnabled
                        HapticUtils.performClick(context)
                    }) {
                        Icon(
                            Icons.Default.Vibration,
                            contentDescription = "Haptics",
                            tint = if (isHapticsEnabled) BreathingCyan else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("breathing_exercise_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header stats
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BreathingCyanBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "4-7-8 RELAXATION TECHNIQUE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BreathingCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isRunning) currentPhase.label else "Ready to Begin",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = animatedColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Cycles: $completedCycles",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }

            // Visual Pulsating Breathing Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                // Pulsing outer aura
                Box(
                    modifier = Modifier
                        .size(animatedSize + 28.dp)
                        .clip(CircleShape)
                        .background(animatedColor.copy(alpha = 0.2f))
                )

                // Main circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(CircleShape)
                        .background(animatedColor)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isRunning) "$countdownSeconds" else "Start",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isRunning) currentPhase.label else "4-7-8 Rhythm",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Instruction guidance card
            Text(
                text = if (isRunning) currentPhase.instruction else "Inhale for 4s, hold gently for 7s, exhale slowly for 8s to calm the nervous system.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Primary Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        HapticUtils.performSuccess(context)
                        isRunning = !isRunning
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFE11D48) else BreathingCyan
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isRunning) "Pause Session" else "Start Breathing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = {
                        HapticUtils.performClick(context)
                        isRunning = false
                        countdownSeconds = 4
                        completedCycles = 0
                        currentPhase = BreathingPhase.INHALE
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
