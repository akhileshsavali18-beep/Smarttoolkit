package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.CompassTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.CompassTealBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompassScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var azimuthDegrees by remember { mutableFloatStateOf(0f) }
    var hasMagneticSensor by remember { mutableStateOf(true) }

    // Register sensor listener
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (sensorManager == null || accelerometer == null || magnetometer == null) {
            hasMagneticSensor = false
        }

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var gravitySet = false
        var geomagneticSet = false

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, gravity, 0, 3)
                    gravitySet = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                    geomagneticSet = true
                }

                if (gravitySet && geomagneticSet) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        azimuth = (azimuth + 360) % 360
                        azimuthDegrees = azimuth
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI) }

        onDispose {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = -azimuthDegrees,
        animationSpec = tween(durationMillis = 150),
        label = "compassRotation"
    )

    val currentAzimuth = azimuthDegrees.roundToInt()
    val cardinalDirection = when (currentAzimuth) {
        in 338..360, in 0..22 -> "North (N)"
        in 23..67 -> "North-East (NE)"
        in 68..112 -> "East (E)"
        in 113..157 -> "South-East (SE)"
        in 158..202 -> "South (S)"
        in 203..247 -> "South-West (SW)"
        in 248..292 -> "West (W)"
        in 293..337 -> "North-West (NW)"
        else -> "North (N)"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Compass & Direction",
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
                        val info = "Heading: $currentAzimuth° $cardinalDirection"
                        copyToClipboard(context, "Compass Heading", info)
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("compass_screen")
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
            // Heading Display
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CompassTealBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "AZIMUTH HEADING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CompassTeal,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "$currentAzimuth°",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CompassTeal
                    )

                    Text(
                        text = cardinalDirection,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Compass Dial Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(270.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 10

                    // Outer dial circle
                    drawCircle(
                        color = Color(0xFF94A3B8),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // 12 ticks
                    for (i in 0 until 360 step 30) {
                        val isCardinal = i % 90 == 0
                        val tickLength = if (isCardinal) 18.dp.toPx() else 10.dp.toPx()
                        val tickColor = if (i == 0) Color(0xFFDC2626) else Color(0xFF64748B)

                        rotate(i.toFloat(), pivot = center) {
                            drawLine(
                                color = tickColor,
                                start = Offset(center.x, center.y - radius),
                                end = Offset(center.x, center.y - radius + tickLength),
                                strokeWidth = if (isCardinal) 3.dp.toPx() else 1.5.dp.toPx()
                            )
                        }
                    }

                    // North Red Arrow Needle
                    val arrowPathNorth = Path().apply {
                        moveTo(center.x, center.y - radius + 22.dp.toPx())
                        lineTo(center.x - 12.dp.toPx(), center.y)
                        lineTo(center.x + 12.dp.toPx(), center.y)
                        close()
                    }
                    drawPath(arrowPathNorth, color = Color(0xFFDC2626))

                    // South Slate Arrow Needle
                    val arrowPathSouth = Path().apply {
                        moveTo(center.x, center.y + radius - 22.dp.toPx())
                        lineTo(center.x - 12.dp.toPx(), center.y)
                        lineTo(center.x + 12.dp.toPx(), center.y)
                        close()
                    }
                    drawPath(arrowPathSouth, color = Color(0xFF475569))

                    // Center pivot point
                    drawCircle(color = Color.White, radius = 6.dp.toPx(), center = center)
                    drawCircle(color = Color(0xFF0F172A), radius = 3.dp.toPx(), center = center)
                }

                // Fixed top indicator marker
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC2626))
                )
            }

            // Sensor information & Calibration advice
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (hasMagneticSensor) Icons.Default.Explore else Icons.Default.Info,
                            contentDescription = null,
                            tint = CompassTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (hasMagneticSensor) "Sensor Active (Smooth Compass Mode)" else "Sensor Fallback Active",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "For maximum magnetic accuracy, keep your device flat on your palm and avoid holding near high electromagnetic fields or magnets.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
