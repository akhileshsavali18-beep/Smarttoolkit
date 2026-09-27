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
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProfitGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.ProfitGreenBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitLossCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var costPriceInput by remember { mutableStateOf("1200") }
    var sellingPriceInput by remember { mutableStateOf("1500") }
    var quantityInput by remember { mutableStateOf("1") }

    val df = remember { DecimalFormat("#,##0.00") }
    val pctDf = remember { DecimalFormat("#,##0.##") }

    val cp = costPriceInput.toDoubleOrNull() ?: 0.0
    val sp = sellingPriceInput.toDoubleOrNull() ?: 0.0
    val qty = quantityInput.toDoubleOrNull()?.coerceAtLeast(1.0) ?: 1.0

    val totalCost = cp * qty
    val totalRevenue = sp * qty
    val netDifference = totalRevenue - totalCost

    val isProfit = netDifference > 0.0
    val isLoss = netDifference < 0.0
    val isBreakEven = netDifference == 0.0

    val percentageOnCost = if (totalCost > 0) (Math.abs(netDifference) / totalCost) * 100.0 else 0.0
    val marginOnSelling = if (totalRevenue > 0) (Math.abs(netDifference) / totalRevenue) * 100.0 else 0.0

    val statusColor = when {
        isProfit -> ProfitGreen
        isLoss -> Color(0xFFDC2626)
        else -> Color(0xFF64748B)
    }

    val statusBg = when {
        isProfit -> ProfitGreenBg
        isLoss -> Color(0xFFFEF2F2)
        else -> Color(0xFFF1F5F9)
    }

    val statusText = when {
        isProfit -> "PROFIT GAIN"
        isLoss -> "NET LOSS"
        else -> "BREAK EVEN"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profit & Loss Calculator",
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
                        costPriceInput = "1000"
                        sellingPriceInput = "1250"
                        quantityInput = "1"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("profit_loss_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    Text(
                        text = "TRANSACTION DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    AppInputField(
                        value = costPriceInput,
                        onValueChange = { costPriceInput = it },
                        label = "Cost Price per Unit (CP)",
                        prefixText = "₹ ",
                        placeholder = "e.g. 1000",
                        testTag = "input_cost_price"
                    )

                    AppInputField(
                        value = sellingPriceInput,
                        onValueChange = { sellingPriceInput = it },
                        label = "Selling Price per Unit (SP)",
                        prefixText = "₹ ",
                        placeholder = "e.g. 1250",
                        testTag = "input_selling_price"
                    )

                    AppInputField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it },
                        label = "Quantity / Units Sold",
                        placeholder = "e.g. 1",
                        testTag = "input_quantity"
                    )
                }
            }

            // Results Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = statusBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLoss) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = statusText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${pctDf.format(percentageOnCost)}% on CP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    Text(
                        text = "₹${df.format(Math.abs(netDifference))}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )

                    HorizontalDivider(color = statusColor.copy(alpha = 0.2f))

                    ResultValueRow(label = "Total Cost (CP × Qty)", value = "₹${df.format(totalCost)}")
                    ResultValueRow(label = "Total Revenue (SP × Qty)", value = "₹${df.format(totalRevenue)}")
                    ResultValueRow(
                        label = if (isLoss) "Loss Percentage" else "Profit Percentage",
                        value = "${pctDf.format(percentageOnCost)}%"
                    )
                    ResultValueRow(
                        label = "Profit Margin (on SP)",
                        value = "${pctDf.format(marginOnSelling)}%"
                    )

                    HorizontalDivider(color = statusColor.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "P&L Summary:\nTotal Cost: ₹${df.format(totalCost)}\nRevenue: ₹${df.format(totalRevenue)}\nResult: $statusText of ₹${df.format(Math.abs(netDifference))} (${pctDf.format(percentageOnCost)}%)"
                                copyToClipboard(context, "P&L Result", text)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy Result", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "Profit & Loss Calculation:\nCost: ₹${df.format(totalCost)}\nRevenue: ₹${df.format(totalRevenue)}\n$statusText: ₹${df.format(Math.abs(netDifference))} (${pctDf.format(percentageOnCost)}%)\n\nCalculated via APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share P&L")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
