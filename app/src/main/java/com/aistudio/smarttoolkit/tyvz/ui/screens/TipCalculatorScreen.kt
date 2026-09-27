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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.TipEmerald
import com.aistudio.smarttoolkit.tyvz.ui.theme.TipEmeraldBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormat = remember { DecimalFormat("₹#,##0.00") }

    var billInput by remember { mutableStateOf("1250") }
    var tipPercent by remember { mutableFloatStateOf(15f) }
    var splitCount by remember { mutableIntStateOf(2) }
    var isRoundUpTotal by remember { mutableStateOf(false) }

    val calculation by remember(billInput, tipPercent, splitCount, isRoundUpTotal) {
        derivedStateOf {
            val bill = billInput.toDoubleOrNull() ?: 0.0
            if (bill <= 0.0) null
            else {
                var rawTip = bill * (tipPercent / 100.0)
                var total = bill + rawTip
                if (isRoundUpTotal) {
                    val roundedTotal = ceil(total)
                    rawTip = roundedTotal - bill
                    total = roundedTotal
                }
                val perPerson = total / splitCount.coerceAtLeast(1)
                val tipPerPerson = rawTip / splitCount.coerceAtLeast(1)

                object {
                    val subtotal = bill
                    val tip = rawTip
                    val effectiveTipPct = if (bill > 0) (rawTip / bill) * 100.0 else tipPercent.toDouble()
                    val grandTotal = total
                    val perPersonTotal = perPerson
                    val perPersonTip = tipPerPerson
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_tip_calculator"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tip & Round-Up Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("tip_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            billInput = "1000"
                            tipPercent = 10f
                            splitCount = 1
                            isRoundUpTotal = false
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = TipEmeraldBg),
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
                            .background(TipEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Restaurant Tip & Bill Share",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TipEmerald
                            )
                        )
                        Text(
                            text = "Compute gracious gratuity, round up totals, and split fairly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input: Bill Amount
            AppInputField(
                value = billInput,
                onValueChange = { billInput = it },
                label = "Bill Amount (Subtotal)",
                prefixText = "₹ ",
                placeholder = "1250",
                testTag = "input_tip_bill_amount"
            )

            // Tip Slider & Preset Chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tip Percentage",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "${tipPercent.toInt()}%",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TipEmerald)
                    )
                }

                Slider(
                    value = tipPercent,
                    onValueChange = { tipPercent = it },
                    valueRange = 0f..30f,
                    steps = 29,
                    modifier = Modifier.testTag("tip_percentage_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = TipEmerald,
                        activeTrackColor = TipEmerald
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5f, 10f, 15f, 18f, 20f).forEach { pct ->
                        FilterChip(
                            selected = tipPercent == pct,
                            onClick = {
                                HapticUtils.performClick(context)
                                tipPercent = pct
                            },
                            label = { Text("${pct.toInt()}%", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TipEmerald,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Split Count Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Split With Friends",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "$splitCount ${if (splitCount == 1) "person" else "people"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (splitCount > 1) {
                                HapticUtils.performClick(context)
                                splitCount--
                            }
                        }
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease people")
                    }

                    Text(
                        text = "$splitCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            if (splitCount < 50) {
                                HapticUtils.performClick(context)
                                splitCount++
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase people")
                    }
                }
            }

            // Round-Up Toggle Chip
            FilterChip(
                selected = isRoundUpTotal,
                onClick = {
                    HapticUtils.performClick(context)
                    isRoundUpTotal = !isRoundUpTotal
                },
                label = { Text(if (isRoundUpTotal) "✓ Round Up Total Enabled" else "Round Up Total to Nearest Rupee") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TipEmerald,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("tip_chip_round_up")
            )

            // Calculation Receipt Card
            if (calculation != null) {
                val c = calculation!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tip_receipt_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Bill Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TipEmerald)
                        )
                        HorizontalDivider()

                        ResultValueRow(
                            label = "Bill Subtotal",
                            value = currencyFormat.format(c.subtotal)
                        )

                        ResultValueRow(
                            label = "Tip Amount (%.1f%%)".format(c.effectiveTipPct),
                            value = currencyFormat.format(c.tip)
                        )

                        ResultValueRow(
                            label = "Grand Total",
                            value = currencyFormat.format(c.grandTotal),
                            isHighlighted = true
                        )

                        if (splitCount > 1) {
                            HorizontalDivider()
                            ResultValueRow(
                                label = "Each Person Pays ($splitCount people)",
                                value = currencyFormat.format(c.perPersonTotal),
                                isHighlighted = true
                            )
                            ResultValueRow(
                                label = "Tip Included Per Person",
                                value = currencyFormat.format(c.perPersonTip)
                            )
                        }
                    }
                }

                // Action Buttons
                val summaryText = "Bill: ${currencyFormat.format(c.subtotal)} | Tip: ${currencyFormat.format(c.tip)} | Total: ${currencyFormat.format(c.grandTotal)} (${if (splitCount > 1) "${currencyFormat.format(c.perPersonTotal)} each for $splitCount people" else "Single payer"}). (Calculated via APS Tools)"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            copyToClipboard(context, summaryText, "Tip Summary")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TipEmerald),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            ImageUtils.shareText(context, summaryText, "Share Bill Tip")
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
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
