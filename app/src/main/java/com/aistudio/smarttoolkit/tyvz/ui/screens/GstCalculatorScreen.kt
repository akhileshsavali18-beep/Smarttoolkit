package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
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
import com.aistudio.smarttoolkit.tyvz.model.Calculators
import com.aistudio.smarttoolkit.tyvz.model.GstMode
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.QuickPercentChip
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.SummaryOutputCard
import com.aistudio.smarttoolkit.tyvz.ui.theme.GstBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.GstBlueBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var amountInput by remember { mutableStateOf("") }
    var customRateInput by remember { mutableStateOf("") }
    var selectedStandardRate by remember { mutableStateOf<Double?>(18.0) }
    var isCustomRate by remember { mutableStateOf(false) }
    var gstMode by remember { mutableStateOf(GstMode.ADD_GST) }

    val activeRate = if (isCustomRate) {
        customRateInput.toDoubleOrNull() ?: 0.0
    } else {
        selectedStandardRate ?: 18.0
    }

    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val result = Calculators.calculateGst(parsedAmount, activeRate, gstMode)

    // Trigger calculation haptic feedback when a valid calculation is computed
    androidx.compose.runtime.LaunchedEffect(result.totalAmount) {
        if (result.totalAmount > 0.0 && parsedAmount > 0.0) {
            HapticUtils.performSuccess(context)
        }
    }

    val copySummary = """
        GST Summary (${if (gstMode == GstMode.ADD_GST) "Add GST" else "Remove GST"})
        Base Amount: ₹${Calculators.formatNumber(result.netAmount)}
        GST Rate: $activeRate%
        CGST (50%): ₹${Calculators.formatNumber(result.cgst)}
        SGST (50%): ₹${Calculators.formatNumber(result.sgst)}
        Total GST: ₹${Calculators.formatNumber(result.totalGst)}
        Total Amount: ₹${Calculators.formatNumber(result.totalAmount)}
    """.trimIndent()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "GST Calculator",
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
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            amountInput = ""
                            customRateInput = ""
                            selectedStandardRate = 18.0
                            isCustomRate = false
                            gstMode = GstMode.ADD_GST
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("reset_gst_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset all fields"
                        )
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Mode Selector: "Add GST" vs "Remove GST"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Add GST option
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (gstMode == GstMode.ADD_GST) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable {
                            HapticUtils.performClick(context)
                            gstMode = GstMode.ADD_GST
                        }
                        .padding(vertical = 12.dp)
                        .testTag("mode_add_gst")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = if (gstMode == GstMode.ADD_GST) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add GST (Exclusive)",
                            fontWeight = if (gstMode == GstMode.ADD_GST) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (gstMode == GstMode.ADD_GST) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Remove GST option
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (gstMode == GstMode.REMOVE_GST) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable {
                            HapticUtils.performClick(context)
                            gstMode = GstMode.REMOVE_GST
                        }
                        .padding(vertical = 12.dp)
                        .testTag("mode_remove_gst")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            tint = if (gstMode == GstMode.REMOVE_GST) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Remove GST (Inclusive)",
                            fontWeight = if (gstMode == GstMode.REMOVE_GST) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (gstMode == GstMode.REMOVE_GST) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input: Amount
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (gstMode == GstMode.ADD_GST) "Enter Net Amount" else "Enter Total Amount (GST Included)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInputField(
                        value = amountInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amountInput = input
                            }
                        },
                        label = "Amount",
                        placeholder = "e.g. 5000",
                        prefixText = "₹",
                        testTag = "gst_amount_input",
                        keyboardType = KeyboardType.Decimal
                    )
                }
            }

            // GST Percentage selection
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "GST Rate (%)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Standard Tax Slabs: 5%, 12%, 18%, 28% and Custom
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5.0, 12.0, 18.0, 28.0).forEach { rate ->
                            QuickPercentChip(
                                label = "${rate.toInt()}%",
                                isSelected = !isCustomRate && selectedStandardRate == rate,
                                onClick = {
                                    isCustomRate = false
                                    selectedStandardRate = rate
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "rate_chip_${rate.toInt()}"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    QuickPercentChip(
                        label = if (isCustomRate) "Custom Rate: ${if (customRateInput.isNotEmpty()) "$customRateInput%" else ""}" else "Custom Rate",
                        isSelected = isCustomRate,
                        onClick = { isCustomRate = true },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "rate_chip_custom"
                    )

                    AnimatedVisibility(visible = isCustomRate) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AppInputField(
                            value = customRateInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    val num = input.toDoubleOrNull()
                                    if (num == null || num <= 100.0) {
                                        customRateInput = input
                                    }
                                }
                            },
                            label = "Custom GST %",
                            placeholder = "Enter custom rate",
                            suffixText = "%",
                            leadingIcon = Icons.Default.Percent,
                            testTag = "custom_rate_input"
                        )
                    }
                }
            }

            // Output Summary Cards
            SummaryOutputCard(
                title = "GST Breakdown",
                accentColor = GstBlue,
                copyText = if (parsedAmount > 0) copySummary else null
            ) {
                ResultValueRow(
                    label = "Net Amount",
                    value = "₹${Calculators.formatNumber(result.netAmount)}",
                    subValue = if (gstMode == GstMode.ADD_GST) "Original amount" else "Excluding GST"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ResultValueRow(
                    label = "CGST (${activeRate / 2.0}%)",
                    value = "₹${Calculators.formatNumber(result.cgst)}",
                    subValue = "Central GST (Half rate)"
                )
                ResultValueRow(
                    label = "SGST (${activeRate / 2.0}%)",
                    value = "₹${Calculators.formatNumber(result.sgst)}",
                    subValue = "State GST (Half rate)"
                )
                ResultValueRow(
                    label = "Total Tax (GST)",
                    value = "₹${Calculators.formatNumber(result.totalGst)}",
                    isHighlighted = true,
                    highlightColor = GstBlue,
                    subValue = "CGST + SGST (${activeRate}%)"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ResultValueRow(
                    label = "Total Amount",
                    value = "₹${Calculators.formatNumber(result.totalAmount)}",
                    isHighlighted = true,
                    highlightColor = MaterialTheme.colorScheme.primary,
                    subValue = "Final payable price"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
