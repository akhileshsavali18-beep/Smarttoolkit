package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.app.Activity
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ads.AdManager
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.model.Calculators
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.QuickPercentChip
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.SummaryOutputCard
import com.aistudio.smarttoolkit.tyvz.ui.theme.CategoryFinanceBg
import com.aistudio.smarttoolkit.tyvz.ui.theme.CategoryFinanceGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.SplitTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.SplitTealBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    onBack: () -> Unit,
    billingManager: BillingManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var billAmountInput by remember { mutableStateOf("1500") }
    var tipPercentInput by remember { mutableStateOf("10") }
    var numPeople by remember { mutableIntStateOf(3) }

    val billAmount = billAmountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipPercentInput.toDoubleOrNull() ?: 0.0
    val result = Calculators.calculateSplitBill(billAmount, tipPercent, numPeople)

    // Calculation completion haptic
    androidx.compose.runtime.LaunchedEffect(result.perPersonTotal) {
        if (result.perPersonTotal > 0.0 && billAmount > 0.0) {
            HapticUtils.performSuccess(context)
        }
    }

    val copySummary = """
        Split Bill Summary
        Total Bill: ₹${Calculators.formatNumber(billAmount)}
        Tip (${tipPercent.toInt()}%): ₹${Calculators.formatNumber(result.tipAmount)}
        Total Payable: ₹${Calculators.formatNumber(result.totalWithTip)}
        Split between: $numPeople people
        Amount Per Person: ₹${Calculators.formatNumber(result.perPersonTotal)}
    """.trimIndent()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Split Bill",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            onBack()
                        },
                        modifier = Modifier.size(48.dp).testTag("back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            billAmountInput = ""
                            tipPercentInput = "0"
                            numPeople = 2
                        },
                        modifier = Modifier.size(48.dp).testTag("reset_split_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Per Person Amount Hero Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SplitTealBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EACH PERSON PAYS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = SplitTeal,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "₹${Calculators.formatNumber(result.perPersonTotal)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Includes ₹${Calculators.formatNumber(result.perPersonTip)} tip each",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Input: Bill Amount
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Total Bill Amount",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInputField(
                        value = billAmountInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                billAmountInput = input
                            }
                        },
                        label = "Bill Amount",
                        placeholder = "e.g. 1500",
                        prefixText = "₹",
                        testTag = "bill_amount_input",
                        keyboardType = KeyboardType.Decimal
                    )
                }
            }

            // Input: Number of People
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = SplitTeal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Split Between",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Stepper (- / +)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (numPeople > 1) {
                                        HapticUtils.performClick(context)
                                        numPeople--
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("decrement_people")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "$numPeople",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (numPeople < 100) {
                                        HapticUtils.performClick(context)
                                        numPeople++
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("increment_people")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Input: Tip Percentage
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tip Percentage",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${tipPercent.toInt()}% (₹${Calculators.formatNumber(result.tipAmount)})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SplitTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("0%", "5%", "10%", "15%", "20%").forEach { pctStr ->
                            val cleanVal = pctStr.replace("%", "")
                            QuickPercentChip(
                                label = pctStr,
                                isSelected = tipPercentInput == cleanVal,
                                onClick = { tipPercentInput = cleanVal },
                                modifier = Modifier.weight(1f),
                                testTag = "tip_chip_$cleanVal"
                            )
                        }
                    }
                }
            }

            // Summary Card
            SummaryOutputCard(
                title = "Total Breakdown",
                accentColor = SplitTeal,
                copyText = if (billAmount > 0) copySummary else null
            ) {
                ResultValueRow(
                    label = "Original Bill",
                    value = "₹${Calculators.formatNumber(billAmount)}"
                )
                ResultValueRow(
                    label = "Tip Amount (${tipPercent.toInt()}%)",
                    value = "₹${Calculators.formatNumber(result.tipAmount)}"
                )
                ResultValueRow(
                    label = "Total Bill with Tip",
                    value = "₹${Calculators.formatNumber(result.totalWithTip)}"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ResultValueRow(
                    label = "Split Among",
                    value = "$numPeople People"
                )
                ResultValueRow(
                    label = "Payable per Person",
                    value = "₹${Calculators.formatNumber(result.perPersonTotal)}",
                    isHighlighted = true,
                    highlightColor = SplitTeal,
                    subValue = "₹${Calculators.formatNumber(result.perPersonBill)} bill + ₹${Calculators.formatNumber(result.perPersonTip)} tip"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
