package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
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
import com.aistudio.smarttoolkit.tyvz.model.EmiTenureType
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.QuickPercentChip
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.SummaryOutputCard
import com.aistudio.smarttoolkit.tyvz.ui.theme.EmiGreen
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEmiScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loanAmountInput by remember { mutableStateOf("1000000") }
    var interestRateInput by remember { mutableStateOf("8.5") }
    var tenureInput by remember { mutableStateOf("5") }
    var tenureType by remember { mutableStateOf(EmiTenureType.YEARS) }

    val principal = loanAmountInput.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateInput.toDoubleOrNull() ?: 0.0
    val tenureValue = tenureInput.toIntOrNull() ?: 0

    val result = Calculators.calculateEmi(principal, interestRate, tenureValue, tenureType)

    // Trigger calculation haptic feedback when a valid EMI is calculated
    androidx.compose.runtime.LaunchedEffect(result.monthlyEmi) {
        if (result.monthlyEmi > 0.0 && principal > 0.0) {
            HapticUtils.performSuccess(context)
        }
    }

    val copySummary = """
        Loan EMI Calculation Summary
        Loan Amount (Principal): ₹${Calculators.formatNumber(principal)}
        Interest Rate: $interestRate% p.a.
        Tenure: $tenureValue ${if (tenureType == EmiTenureType.YEARS) "Years" else "Months"}
        Monthly EMI: ₹${Calculators.formatNumber(result.monthlyEmi)}
        Total Interest Payable: ₹${Calculators.formatNumber(result.totalInterest)}
        Total Payment: ₹${Calculators.formatNumber(result.totalPayment)}
    """.trimIndent()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Loan EMI Calculator",
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
                            loanAmountInput = ""
                            interestRateInput = ""
                            tenureInput = ""
                            tenureType = EmiTenureType.YEARS
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("reset_emi_button")
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
            // Featured Monthly EMI Highlight Banner Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MONTHLY EMI PAYABLE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹${Calculators.formatNumber(result.monthlyEmi)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "for $tenureValue ${if (tenureType == EmiTenureType.YEARS) "Years" else "Months"} at $interestRate% interest",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Input: Loan Amount
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Loan Amount (Principal)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInputField(
                        value = loanAmountInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                loanAmountInput = input
                            }
                        },
                        label = "Principal Amount",
                        placeholder = "e.g. 500000",
                        prefixText = "₹",
                        testTag = "loan_amount_input",
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // Quick amount shortcuts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1 Lakh" to "100000", "5 Lakh" to "500000", "10 Lakh" to "1000000", "25 Lakh" to "2500000").forEach { (lbl, amt) ->
                            QuickPercentChip(
                                label = lbl,
                                isSelected = loanAmountInput == amt,
                                onClick = { loanAmountInput = amt },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_amount_${amt}"
                            )
                        }
                    }
                }
            }

            // Input: Annual Interest Rate
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Annual Interest Rate (%)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInputField(
                        value = interestRateInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                interestRateInput = input
                            }
                        },
                        label = "Interest Rate",
                        placeholder = "e.g. 8.5",
                        suffixText = "% p.a.",
                        leadingIcon = Icons.Default.Percent,
                        testTag = "interest_rate_input",
                        keyboardType = KeyboardType.Decimal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // Quick rate shortcuts (typical home, car, personal loan rates)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("7.5%", "8.5%", "9.5%", "11.0%").forEach { rateStr ->
                            val cleanVal = rateStr.replace("%", "")
                            QuickPercentChip(
                                label = rateStr,
                                isSelected = interestRateInput == cleanVal,
                                onClick = { interestRateInput = cleanVal },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_rate_${cleanVal}"
                            )
                        }
                    }
                }
            }

            // Input: Loan Tenure with Years / Months switch
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
                            text = "Loan Tenure",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Toggle Buttons: Years vs Months
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(2.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (tenureType == EmiTenureType.YEARS) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        HapticUtils.performClick(context)
                                        tenureType = EmiTenureType.YEARS
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("tenure_years_toggle")
                            ) {
                                Text(
                                    text = "Years",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tenureType == EmiTenureType.YEARS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (tenureType == EmiTenureType.MONTHS) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        HapticUtils.performClick(context)
                                        tenureType = EmiTenureType.MONTHS
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("tenure_months_toggle")
                            ) {
                                Text(
                                    text = "Months",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tenureType == EmiTenureType.MONTHS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    AppInputField(
                        value = tenureInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*$"))) {
                                tenureInput = input
                            }
                        },
                        label = if (tenureType == EmiTenureType.YEARS) "Duration in Years" else "Duration in Months",
                        placeholder = if (tenureType == EmiTenureType.YEARS) "e.g. 5" else "e.g. 60",
                        leadingIcon = Icons.Default.CalendarMonth,
                        suffixText = if (tenureType == EmiTenureType.YEARS) "Yrs" else "Mos",
                        testTag = "tenure_input",
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            // Output Summary Card
            SummaryOutputCard(
                title = "Payment Breakdown",
                accentColor = EmiGreen,
                copyText = if (principal > 0 && tenureValue > 0) copySummary else null
            ) {
                // Visual ratio progress bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Principal (${String.format("%.1f", result.principalPercentage)}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmiGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Interest (${String.format("%.1f", result.interestPercentage)}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val animatedPrincipalWeight by animateFloatAsState(
                        targetValue = (result.principalPercentage / 100f).coerceIn(0.01f, 0.99f),
                        label = "principal_bar"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(animatedPrincipalWeight)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f - animatedPrincipalWeight)
                                .background(EmiGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ResultValueRow(
                    label = "Monthly EMI",
                    value = "₹${Calculators.formatNumber(result.monthlyEmi)}",
                    isHighlighted = true,
                    highlightColor = MaterialTheme.colorScheme.primary,
                    subValue = "Monthly installment"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ResultValueRow(
                    label = "Total Interest Payable",
                    value = "₹${Calculators.formatNumber(result.totalInterest)}",
                    highlightColor = EmiGreen,
                    subValue = "Interest over tenure"
                )
                ResultValueRow(
                    label = "Principal Loan Amount",
                    value = "₹${Calculators.formatNumber(principal)}",
                    subValue = "Borrowed capital"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ResultValueRow(
                    label = "Total Payment (Principal + Interest)",
                    value = "₹${Calculators.formatNumber(result.totalPayment)}",
                    isHighlighted = true,
                    highlightColor = MaterialTheme.colorScheme.primary,
                    subValue = "Total amount repaid"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
