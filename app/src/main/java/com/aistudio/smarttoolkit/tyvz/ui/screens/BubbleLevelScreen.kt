package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.theme.LevelLime
import com.aistudio.smarttoolkit.tyvz.ui.theme.LevelLimeBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import java.text.DecimalFormat
import kotlin.math.atan2
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleLevelScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rawPitch by remember { mutableFloatStateOf(0f) }
    var rawRoll by remember { mutableFloatStateOf(0f) }
    var calibPitch by remember { mutableFloatStateOf(0f) }
    var calibRoll by remember { mutableFloatStateOf(0f) }

    val df = remember { DecimalFormat("0.0") }

    // Sensor registration
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]

                // Low pass filtered pitch & roll calculation
                val pitch = Math.toDegrees(atan2(ay.toDouble(), az.toDouble())).toFloat()
                val roll = Math.toDegrees(atan2(-ax.toDouble(), sqrt((ay * ay + az * az).toDouble()))).toFloat()

                rawPitch = pitch
                rawRoll = roll
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val pitch = rawPitch - calibPitch
    val roll = rawRoll - calibRoll

    val isLevel = Math.abs(pitch) <= 0.6f && Math.abs(roll) <= 0.6f

    // Trigger soft tick when level achieved
    LaunchedEffect(isLevel) {
        if (isLevel) {
            HapticUtils.performTick(context)
        }
    }

    val activeColor by animateColorAsState(
        targetValue = if (isLevel) Color(0xFF16A34A) else LevelLime,
        animationSpec = tween(150),
        label = "levelColor"
    )

    val animatedPitch by animateFloatAsState(targetValue = pitch, label = "pitchAnim")
    val animatedRoll by animateFloatAsState(targetValue = roll, label = "rollAnim")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Bubble Spirit Level",
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
                        calibPitch = rawPitch
                        calibRoll = rawRoll
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Zero Calibrate")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("bubble_level_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = if (isLevel) Color(0xFFDCFCE7) else LevelLimeBg),
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
                            text = if (isLevel) "PERFECTLY LEVEL" else "TILT ANGLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isLevel) "0.0°" else "${df.format(Math.max(Math.abs(pitch), Math.abs(roll)))}°",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = activeColor
                        )
                    }

                    if (isLevel) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Circular Bullseye Level
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .padding(10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val maxRadius = size.width / 2 - 8.dp.toPx()

                    // Rings
                    drawCircle(color = Color(0xFFCBD5E1), radius = maxRadius, style = Stroke(2.dp.toPx()))
                    drawCircle(color = Color(0xFFCBD5E1), radius = maxRadius * 0.66f, style = Stroke(1.5.dp.toPx()))
                    drawCircle(color = activeColor, radius = maxRadius * 0.33f, style = Stroke(2.dp.toPx()))

                    // Crosshair lines
                    drawLine(
                        color = Color(0xFF94A3B8),
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF94A3B8),
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Bubble offset based on roll (X) and pitch (Y)
                    val bubbleRadius = 24.dp.toPx()
                    val offsetX = (animatedRoll / 45f).coerceIn(-1f, 1f) * (maxRadius - bubbleRadius)
                    val offsetY = (animatedPitch / 45f).coerceIn(-1f, 1f) * (maxRadius - bubbleRadius)
                    val bubbleCenter = Offset(center.x + offsetX, center.y + offsetY)

                    // Bubble glow & fill
                    drawCircle(
                        color = activeColor.copy(alpha = 0.3f),
                        radius = bubbleRadius + 4.dp.toPx(),
                        center = bubbleCenter
                    )
                    drawCircle(
                        color = activeColor,
                        radius = bubbleRadius,
                        center = bubbleCenter
                    )
                    // Specular highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.7f),
                        radius = bubbleRadius * 0.4f,
                        center = Offset(bubbleCenter.x - bubbleRadius * 0.25f, bubbleCenter.y - bubbleRadius * 0.25f)
                    )
                }
            }

            // Linear Angle Details Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResultValueRow(label = "X-Axis Roll (Horizontal)", value = "${df.format(roll)}°")
                    ResultValueRow(label = "Y-Axis Pitch (Vertical)", value = "${df.format(pitch)}°")

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            calibPitch = rawPitch
                            calibRoll = rawRoll
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LevelLime),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Calibrate / Set Current as Zero", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
