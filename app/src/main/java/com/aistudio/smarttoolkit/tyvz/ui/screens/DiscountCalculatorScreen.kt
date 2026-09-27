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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.QuickPercentChip
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.SummaryOutputCard
import com.aistudio.smarttoolkit.tyvz.ui.theme.DiscountPurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.DiscountPurpleBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var originalPriceInput by remember { mutableStateOf("2500") }
    var discountPercentInput by remember { mutableStateOf("20") }

    val originalPrice = originalPriceInput.toDoubleOrNull() ?: 0.0
    val discountPercent = discountPercentInput.toDoubleOrNull() ?: 0.0

    val result = Calculators.calculateDiscount(originalPrice, discountPercent)

    // Trigger calculation haptic feedback when a valid calculation is computed
    androidx.compose.runtime.LaunchedEffect(result.finalPrice) {
        if (result.finalPrice > 0.0 && originalPrice > 0.0) {
            HapticUtils.performSuccess(context)
        }
    }

    val copySummary = """
        Discount Calculation Summary
        Original Price: ₹${Calculators.formatNumber(result.originalPrice)}
        Discount: ${result.discountPercent}%
        You Save: ₹${Calculators.formatNumber(result.discountAmount)}
        Final Payable Price: ₹${Calculators.formatNumber(result.finalPrice)}
    """.trimIndent()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Discount Calculator",
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
                            originalPriceInput = ""
                            discountPercentInput = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("reset_discount_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset inputs"
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
            // Highlight Offer Banner Card (Final Price & Total Savings)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DiscountPurpleBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DiscountPurple)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${discountPercent.toInt()}% OFF",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FINAL PAYABLE PRICE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = DiscountPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${Calculators.formatNumber(result.finalPrice)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Saved",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "You save ₹${Calculators.formatNumber(result.discountAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Input: Original Price
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Original Price",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInputField(
                        value = originalPriceInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                originalPriceInput = input
                            }
                        },
                        label = "Price before discount",
                        placeholder = "e.g. 2499",
                        prefixText = "₹",
                        testTag = "original_price_input",
                        keyboardType = KeyboardType.Decimal
                    )
                }
            }

            // Input: Discount Percentage
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
                            text = "Discount Percentage",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${discountPercent.toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DiscountPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    AppInputField(
                        value = discountPercentInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,1}$"))) {
                                val num = input.toDoubleOrNull()
                                if (num == null || num <= 100.0) {
                                    discountPercentInput = input
                                }
                            }
                        },
                        label = "Discount (%)",
                        placeholder = "e.g. 20",
                        suffixText = "%",
                        leadingIcon = Icons.Default.Percent,
                        testTag = "discount_percent_input",
                        keyboardType = KeyboardType.Decimal
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // Slider for quick discount dragging
                    Slider(
                        value = discountPercent.toFloat().coerceIn(0f, 100f),
                        onValueChange = { newValue ->
                            discountPercentInput = newValue.toInt().toString()
                        },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = DiscountPurple,
                            activeTrackColor = DiscountPurple
                        ),
                        modifier = Modifier.testTag("discount_slider")
                    )

                    // Quick Discount Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("10%", "20%", "30%", "40%", "50%").forEach { pctStr ->
                            val cleanVal = pctStr.replace("%", "")
                            QuickPercentChip(
                                label = pctStr,
                                isSelected = discountPercentInput == cleanVal,
                                onClick = { discountPercentInput = cleanVal },
                                modifier = Modifier.weight(1f),
                                testTag = "discount_chip_$cleanVal"
                            )
                        }
                    }
                }
            }

            // Summary Breakdown Card
            SummaryOutputCard(
                title = "Price Breakdown",
                accentColor = DiscountPurple,
                copyText = if (originalPrice > 0) copySummary else null
            ) {
                ResultValueRow(
                    label = "Original Price (MRP)",
                    value = "₹${Calculators.formatNumber(originalPrice)}",
                    subValue = "List price"
                )
                ResultValueRow(
                    label = "Discount (${result.discountPercent.toInt()}%)",
                    value = "- ₹${Calculators.formatNumber(result.discountAmount)}",
                    isHighlighted = true,
                    highlightColor = MaterialTheme.colorScheme.tertiary,
                    subValue = "Direct savings"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ResultValueRow(
                    label = "Final Price",
                    value = "₹${Calculators.formatNumber(result.finalPrice)}",
                    isHighlighted = true,
                    highlightColor = DiscountPurple,
                    subValue = "You pay"
                )
                ResultValueRow(
                    label = "Total Savings",
                    value = "₹${Calculators.formatNumber(result.discountAmount)}",
                    subValue = "${result.discountPercent.toInt()}% reduced"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
