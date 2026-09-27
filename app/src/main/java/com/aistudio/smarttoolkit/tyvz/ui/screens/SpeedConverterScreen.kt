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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.SpeedOrange
import com.aistudio.smarttoolkit.tyvz.ui.theme.SpeedOrangeBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedConverterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val df = remember { DecimalFormat("#,##0.####") }

    // Store baseline speed in km/h
    var kmhStr by remember { mutableStateOf("100") }
    var mphStr by remember { mutableStateOf("62.1371") }
    var msStr by remember { mutableStateOf("27.7778") }
    var knotsStr by remember { mutableStateOf("53.9957") }
    var ftsStr by remember { mutableStateOf("91.1344") }
    var machStr by remember { mutableStateOf("0.0816") }

    fun updateAllFromKmh(kmh: Double) {
        kmhStr = df.format(kmh)
        mphStr = df.format(kmh * 0.6213711922)
        msStr = df.format(kmh / 3.6)
        knotsStr = df.format(kmh * 0.539956803)
        ftsStr = df.format(kmh * 0.911344415)
        machStr = df.format(kmh / 1225.044)
    }

    Scaffold(
        modifier = modifier.testTag("screen_speed_converter"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Speed & Velocity Converter",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("speed_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            updateAllFromKmh(0.0)
                            kmhStr = ""
                            mphStr = ""
                            msStr = ""
                            knotsStr = ""
                            ftsStr = ""
                            machStr = ""
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SpeedOrangeBg),
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
                            .background(SpeedOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Velocity & Speed Units",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SpeedOrange
                            )
                        )
                        Text(
                            text = "Real-time bidirectional conversion across international speed units",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf(
                    "Walking (5 km/h)" to 5.0,
                    "Cycling (20)" to 20.0,
                    "Highway (100)" to 100.0,
                    "Airliner (900)" to 900.0
                )
                presets.forEach { (label, speed) ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            HapticUtils.performClick(context)
                            updateAllFromKmh(speed)
                        },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            // Unit 1: Kilometers per Hour (km/h)
            SpeedUnitRow(
                label = "Kilometers per hour (km/h)",
                value = kmhStr,
                onValueChange = {
                    kmhStr = it
                    it.toDoubleOrNull()?.let { kmh -> updateAllFromKmh(kmh) }
                },
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, "$kmhStr km/h", "Speed in km/h")
                },
                testTag = "input_speed_kmh"
            )

            // Unit 2: Miles per Hour (mph)
            SpeedUnitRow(
                label = "Miles per hour (mph)",
                value = mphStr,
                onValueChange = {
                    mphStr = it
                    it.toDoubleOrNull()?.let { mph -> updateAllFromKmh(mph / 0.6213711922) }
                },
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, "$mphStr mph", "Speed in mph")
                },
                testTag = "input_speed_mph"
            )

            // Unit 3: Meters per Second (m/s)
            SpeedUnitRow(
                label = "Meters per second (m/s)",
                value = msStr,
                onValueChange = {
                    msStr = it
                    it.toDoubleOrNull()?.let { ms -> updateAllFromKmh(ms * 3.6) }
                },
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, "$msStr m/s", "Speed in m/s")
                },
                testTag = "input_speed_ms"
            )

            // Unit 4: Knots (kn)
            SpeedUnitRow(
                label = "Knots (Nautical miles / hour)",
                value = knotsStr,
                onValueChange = {
                    knotsStr = it
                    it.toDoubleOrNull()?.let { kn -> updateAllFromKmh(kn / 0.539956803) }
                },
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, "$knotsStr kn", "Speed in knots")
                },
                testTag = "input_speed_knots"
            )

            // Unit 5: Feet per Second (ft/s)
            SpeedUnitRow(
                label = "Feet per second (ft/s)",
                value = ftsStr,
                onValueChange = {
                    ftsStr = it
                    it.toDoubleOrNull()?.let { fts -> updateAllFromKmh(fts / 0.911344415) }
                },
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, "$ftsStr ft/s", "Speed in ft/s")
                },
                testTag = "input_speed_fts"
            )

            // Unit 6: Mach (Speed of Sound)
            SpeedUnitRow(
                label = "Mach (Ratio to speed of sound in air)",
                value = machStr,
                onValueChange = {
                    machStr = it
                    it.toDoubleOrNull()?.let { m -> updateAllFromKmh(m * 1225.044) }
                },
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, "Mach $machStr", "Speed in Mach")
                },
                testTag = "input_speed_mach"
            )

            // Share Summary Button
            val shareSummary = "$kmhStr km/h = $mphStr mph = $msStr m/s = $knotsStr knots (Converted by APS Tools)"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        HapticUtils.performSuccess(context)
                        copyToClipboard(context, shareSummary, "Speed Conversion")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SpeedOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy All")
                }

                Button(
                    onClick = {
                        HapticUtils.performClick(context)
                        ImageUtils.shareText(context, shareSummary, "Share Speed Conversion")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SpeedUnitRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onCopy: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SpeedOrange)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(testTag),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy $label")
                }
            }
        }
    }
}
