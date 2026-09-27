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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.InflationRed
import com.aistudio.smarttoolkit.tyvz.ui.theme.InflationRedBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat
import kotlin.math.pow

data class InflationResult(
    val presentValue: Double,
    val inflationRate: Double,
    val years: Int,
    val futureCost: Double,
    val futurePurchasingPower: Double,
    val cumulativeInflationPct: Double,
    val purchasingPowerLossPct: Double,
    val milestones: List<Pair<Int, Double>> // Year to FutureCost
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InflationCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormat = remember { DecimalFormat("₹#,##0.00") }
    val pctFormat = remember { DecimalFormat("#,##0.0'%'") }

    var currentAmountInput by remember { mutableStateOf("100000") }
    var inflationRateInput by remember { mutableStateOf("6.0") }
    var yearsInput by remember { mutableStateOf("10") }

    val result by remember(currentAmountInput, inflationRateInput, yearsInput) {
        derivedStateOf {
            val pv = currentAmountInput.toDoubleOrNull() ?: 0.0
            val rate = inflationRateInput.toDoubleOrNull() ?: 0.0
            val years = yearsInput.toIntOrNull() ?: 0

            if (pv > 0.0 && rate >= 0.0 && years > 0) {
                val r = rate / 100.0
                val multiplier = (1.0 + r).pow(years.toDouble())
                val futureCost = pv * multiplier
                val futurePurchasingPower = pv / multiplier
                val cumulativeInflation = ((futureCost - pv) / pv) * 100.0
                val lossPct = ((pv - futurePurchasingPower) / pv) * 100.0

                val milestones = listOf(5, 10, 15, 20, 25).filter { it <= years * 2 }.map { y ->
                    Pair(y, pv * (1.0 + r).pow(y.toDouble()))
                }

                InflationResult(
                    presentValue = pv,
                    inflationRate = rate,
                    years = years,
                    futureCost = futureCost,
                    futurePurchasingPower = futurePurchasingPower,
                    cumulativeInflationPct = cumulativeInflation,
                    purchasingPowerLossPct = lossPct,
                    milestones = milestones
                )
            } else null
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_inflation"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Inflation & Purchasing Power",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("inflation_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            currentAmountInput = "100000"
                            inflationRateInput = "6.0"
                            yearsInput = "10"
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
                colors = CardDefaults.cardColors(containerColor = InflationRedBg),
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
                            .background(InflationRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Inflation & Money Value",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InflationRed
                            )
                        )
                        Text(
                            text = "Calculate future price increases and the erosion of purchasing power",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input 1: Current Cost / Amount
            AppInputField(
                value = currentAmountInput,
                onValueChange = { currentAmountInput = it },
                label = "Current Amount / Price",
                prefixText = "₹ ",
                placeholder = "100000",
                testTag = "input_inflation_current_amount"
            )

            // Input 2: Inflation Rate (%)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AppInputField(
                    value = inflationRateInput,
                    onValueChange = { inflationRateInput = it },
                    label = "Expected Annual Inflation Rate (%)",
                    suffixText = "%",
                    placeholder = "6.0",
                    testTag = "input_inflation_rate"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("4.0", "5.0", "6.0", "7.0", "8.0").forEach { rate ->
                        FilterChip(
                            selected = inflationRateInput == rate,
                            onClick = {
                                HapticUtils.performClick(context)
                                inflationRateInput = rate
                            },
                            label = { Text("$rate%", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Input 3: Time Horizon (Years)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AppInputField(
                    value = yearsInput,
                    onValueChange = { yearsInput = it },
                    label = "Time Horizon (Years)",
                    suffixText = "Years",
                    placeholder = "10",
                    testTag = "input_inflation_years"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("5", "10", "15", "20", "25").forEach { y ->
                        FilterChip(
                            selected = yearsInput == y,
                            onClick = {
                                HapticUtils.performClick(context)
                                yearsInput = y
                            },
                            label = { Text("$y Yrs", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Result Card
            if (result != null) {
                val r = result!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inflation_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Inflation Impact Projection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = InflationRed)
                        )
                        HorizontalDivider()

                        ResultValueRow(
                            label = "Future Cost (in ${r.years} years)",
                            value = currencyFormat.format(r.futureCost),
                            isHighlighted = true
                        )

                        ResultValueRow(
                            label = "Future Purchasing Value of ₹${currentAmountInput}",
                            value = currencyFormat.format(r.futurePurchasingPower)
                        )

                        ResultValueRow(
                            label = "Cumulative Price Increase",
                            value = "+${pctFormat.format(r.cumulativeInflationPct)}"
                        )

                        ResultValueRow(
                            label = "Purchasing Power Loss",
                            value = "-${pctFormat.format(r.purchasingPowerLossPct)}"
                        )

                        // Milestone Projection Table
                        if (r.milestones.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Milestone Projections:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            r.milestones.forEach { (yr, cost) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "In $yr Years",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = currencyFormat.format(cost),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = InflationRed
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                val shareSummary = "At ${r.inflationRate}% annual inflation, ${currencyFormat.format(r.presentValue)} today will cost ${currencyFormat.format(r.futureCost)} in ${r.years} years (+${pctFormat.format(r.cumulativeInflationPct)} increase). (Calculated via APS Tools)"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            copyToClipboard(context, shareSummary, "Inflation Summary")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = InflationRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            ImageUtils.shareText(context, shareSummary, "Share Inflation Analysis")
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
