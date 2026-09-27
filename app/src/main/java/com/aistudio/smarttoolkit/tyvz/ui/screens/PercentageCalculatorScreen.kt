package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.PercentOrange
import com.aistudio.smarttoolkit.tyvz.ui.theme.PercentOrangeBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat

enum class PercentageMode(val title: String) {
    VALUE_OF("X% of Y"),
    PERCENT_CHANGE("% Change"),
    WHAT_PERCENT("X is ?% of Y")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PercentageCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(PercentageMode.VALUE_OF) }

    var inputX by remember { mutableStateOf("15") }
    var inputY by remember { mutableStateOf("250") }

    val df = remember { DecimalFormat("#,##0.##") }

    val valX = inputX.toDoubleOrNull() ?: 0.0
    val valY = inputY.toDoubleOrNull() ?: 0.0

    // Calculations based on mode
    val calculationResult = remember(selectedMode, valX, valY) {
        when (selectedMode) {
            PercentageMode.VALUE_OF -> {
                val ans = (valX * valY) / 100.0
                val formula = "$valX% of $valY = ($valX × $valY) ÷ 100"
                Triple(df.format(ans), formula, ans)
            }
            PercentageMode.PERCENT_CHANGE -> {
                if (valX == 0.0) {
                    Triple("0%", "Cannot calculate change from 0", 0.0)
                } else {
                    val change = ((valY - valX) / valX) * 100.0
                    val sign = if (change > 0) "+" else ""
                    val type = if (change >= 0) "Increase" else "Decrease"
                    val formula = "Change from $valX to $valY: (($valY - $valX) ÷ $valX) × 100"
                    Triple("$sign${df.format(change)}% ($type)", formula, change)
                }
            }
            PercentageMode.WHAT_PERCENT -> {
                if (valY == 0.0) {
                    Triple("0%", "Division by zero", 0.0)
                } else {
                    val pct = (valX / valY) * 100.0
                    val formula = "$valX is ($valX ÷ $valY) × 100 of $valY"
                    Triple("${df.format(pct)}%", formula, pct)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Percentage Calculator",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputX = "10"
                            inputY = "100"
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("percentage_calc_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PercentageMode.values().forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = {
                            HapticUtils.performClick(context)
                            selectedMode = mode
                        },
                        label = { Text(mode.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PercentOrange,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

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
                    val (labelX, labelY) = when (selectedMode) {
                        PercentageMode.VALUE_OF -> "Percentage (%)" to "Of Amount / Total (Y)"
                        PercentageMode.PERCENT_CHANGE -> "Initial Value (From X)" to "Final Value (To Y)"
                        PercentageMode.WHAT_PERCENT -> "Part Value (X)" to "Total Whole (Y)"
                    }

                    AppInputField(
                        value = inputX,
                        onValueChange = { inputX = it },
                        label = labelX,
                        placeholder = "Enter number",
                        testTag = "input_percent_x"
                    )

                    AppInputField(
                        value = inputY,
                        onValueChange = { inputY = it },
                        label = labelY,
                        placeholder = "Enter total amount",
                        testTag = "input_percent_y"
                    )
                }
            }

            // Result Display Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PercentOrangeBg
                ),
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
                        Text(
                            text = "CALCULATED RESULT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PercentOrange,
                            letterSpacing = 1.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PercentOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedMode.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PercentOrange
                            )
                        }
                    }

                    Text(
                        text = calculationResult.first,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF9A3412)
                    )

                    Text(
                        text = calculationResult.second,
                        fontSize = 13.sp,
                        color = Color(0xFF7C2D12).copy(alpha = 0.8f)
                    )

                    HorizontalDivider(
                        color = PercentOrange.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = "${selectedMode.title}: ${calculationResult.first} (${calculationResult.second})"
                                copyToClipboard(context, "Percentage Result", textToCopy)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PercentOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy Result", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "Percentage Calculation:\n${calculationResult.second}\n= ${calculationResult.first}\n\nCalculated with APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share Result")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = PercentOrange, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = PercentOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
