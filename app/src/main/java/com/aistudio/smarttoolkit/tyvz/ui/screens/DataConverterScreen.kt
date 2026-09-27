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
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.DataBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.DataBlueBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat
import kotlin.math.pow

data class DataUnit(
    val name: String,
    val symbol: String,
    val power: Int // power of 1024 or 1000
)

val dataUnits = listOf(
    DataUnit("Bytes", "B", 0),
    DataUnit("Kilobytes", "KB", 1),
    DataUnit("Megabytes", "MB", 2),
    DataUnit("Gigabytes", "GB", 3),
    DataUnit("Terabytes", "TB", 4),
    DataUnit("Petabytes", "PB", 5)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataConverterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputAmount by remember { mutableStateOf("10") }
    var fromUnitIndex by remember { mutableIntStateOf(3) } // GB
    var isBinaryBase by remember { mutableStateOf(true) } // 1024 vs 1000
    var speedMbpsInput by remember { mutableStateOf("100") } // 100 Mbps

    var fromExpanded by remember { mutableStateOf(false) }

    val base = if (isBinaryBase) 1024.0 else 1000.0
    val amount = inputAmount.toDoubleOrNull() ?: 0.0
    val totalBytes = amount * base.pow(dataUnits[fromUnitIndex].power.toDouble())

    val df = remember { DecimalFormat("#,##0.######") }

    // Transfer time calculation: total bits / (speed in bits per second)
    val speedMbps = speedMbpsInput.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 100.0
    val totalBits = totalBytes * 8.0
    val speedBps = speedMbps * 1_000_000.0
    val transferSeconds = totalBits / speedBps

    val formattedTransferTime = remember(transferSeconds) {
        val hrs = (transferSeconds / 3600).toInt()
        val mins = ((transferSeconds % 3600) / 60).toInt()
        val secs = (transferSeconds % 60).toInt()
        when {
            hrs > 0 -> "${hrs}h ${mins}m ${secs}s"
            mins > 0 -> "${mins}m ${secs}s"
            else -> "${secs}s"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Data & Storage Converter",
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
                        inputAmount = "1"
                        fromUnitIndex = 3
                        isBinaryBase = true
                        speedMbpsInput = "100"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("data_converter_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Inputs Card
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
                        text = "DATA INPUT & STANDARD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    // Binary 1024 vs Decimal 1000 chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = isBinaryBase,
                            onClick = {
                                HapticUtils.performClick(context)
                                isBinaryBase = true
                            },
                            label = { Text("Binary (1024 B / KiB)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DataBlue,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = !isBinaryBase,
                            onClick = {
                                HapticUtils.performClick(context)
                                isBinaryBase = false
                            },
                            label = { Text("Decimal (1000 B / SI)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DataBlue,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AppInputField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = "Storage Size Value",
                        suffixText = dataUnits[fromUnitIndex].symbol,
                        placeholder = "e.g. 10",
                        testTag = "input_data_amount"
                    )

                    ExposedDropdownMenuBox(
                        expanded = fromExpanded,
                        onExpandedChange = { fromExpanded = !fromExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${dataUnits[fromUnitIndex].name} (${dataUnits[fromUnitIndex].symbol})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Source Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = fromExpanded,
                            onDismissRequest = { fromExpanded = false }
                        ) {
                            dataUnits.forEachIndexed { idx, unit ->
                                DropdownMenuItem(
                                    text = { Text("${unit.name} (${unit.symbol})") },
                                    onClick = {
                                        fromUnitIndex = idx
                                        fromExpanded = false
                                        HapticUtils.performClick(context)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Transfer Time Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DataBlueBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = DataBlue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "TRANSFER TIME ESTIMATE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DataBlue,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DataBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "@ ${speedMbps.toInt()} Mbps",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DataBlue
                            )
                        }
                    }

                    Text(
                        text = formattedTransferTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF075985)
                    )

                    AppInputField(
                        value = speedMbpsInput,
                        onValueChange = { speedMbpsInput = it },
                        label = "Internet Speed",
                        suffixText = "Mbps",
                        placeholder = "e.g. 100",
                        testTag = "input_speed_mbps"
                    )
                }
            }

            // All Equivalent Units Breakdown
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
                    Text(
                        text = "ALL STORAGE EQUIVALENTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    dataUnits.forEach { unit ->
                        val valueInUnit = totalBytes / base.pow(unit.power.toDouble())
                        ResultValueRow(
                            label = "${unit.name} (${unit.symbol})",
                            value = df.format(valueInUnit)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Button(
                        onClick = {
                            val text = dataUnits.joinToString("\n") { u ->
                                val v = totalBytes / base.pow(u.power.toDouble())
                                "${u.name} (${u.symbol}): ${df.format(v)}"
                            }
                            copyToClipboard(context, "Data Equivalents", text)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DataBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Copy All Equivalents", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
