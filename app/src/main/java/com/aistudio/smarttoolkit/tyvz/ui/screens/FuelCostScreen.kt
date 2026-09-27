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
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.FuelAmber
import com.aistudio.smarttoolkit.tyvz.ui.theme.FuelAmberBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCostScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var distanceInput by remember { mutableStateOf("250") }
    var mileageInput by remember { mutableStateOf("18.0") }
    var fuelPriceInput by remember { mutableStateOf("96.5") }
    var isRoundTrip by remember { mutableStateOf(false) }
    var passengerCount by remember { mutableIntStateOf(1) }

    val df = remember { DecimalFormat("#,##0.00") }
    val literDf = remember { DecimalFormat("#,##0.1") }

    val baseDistance = distanceInput.toDoubleOrNull() ?: 0.0
    val totalDistance = if (isRoundTrip) baseDistance * 2 else baseDistance
    val mileage = mileageInput.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0
    val fuelPrice = fuelPriceInput.toDoubleOrNull() ?: 0.0

    val totalLiters = totalDistance / mileage
    val totalCost = totalLiters * fuelPrice
    val costPerKm = if (totalDistance > 0) totalCost / totalDistance else 0.0
    val costPerPerson = totalCost / passengerCount.coerceAtLeast(1)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Fuel Cost & Mileage",
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
                        distanceInput = "200"
                        mileageInput = "15.0"
                        fuelPriceInput = "96.5"
                        isRoundTrip = false
                        passengerCount = 1
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("fuel_cost_screen")
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
                        text = "TRIP & VEHICLE SPECS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    AppInputField(
                        value = distanceInput,
                        onValueChange = { distanceInput = it },
                        label = "One-way Trip Distance",
                        suffixText = "km",
                        placeholder = "e.g. 250",
                        testTag = "input_distance"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Round Trip (Return)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Doubles total travel distance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isRoundTrip,
                            onCheckedChange = {
                                HapticUtils.performClick(context)
                                isRoundTrip = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = FuelAmber, checkedTrackColor = FuelAmber.copy(alpha = 0.3f))
                        )
                    }

                    AppInputField(
                        value = mileageInput,
                        onValueChange = { mileageInput = it },
                        label = "Vehicle Mileage / Fuel Economy",
                        suffixText = "km/L",
                        placeholder = "e.g. 18.0",
                        testTag = "input_mileage"
                    )

                    AppInputField(
                        value = fuelPriceInput,
                        onValueChange = { fuelPriceInput = it },
                        label = "Fuel Price per Liter",
                        prefixText = "₹ ",
                        placeholder = "e.g. 96.5",
                        testTag = "input_fuel_price"
                    )

                    // Passenger Split Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Split with Passengers", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (passengerCount > 1) {
                                        HapticUtils.performClick(context)
                                        passengerCount--
                                    }
                                }
                            ) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$passengerCount ${if (passengerCount == 1) "Person" else "People"}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (passengerCount < 10) {
                                        HapticUtils.performClick(context)
                                        passengerCount++
                                    }
                                }
                            ) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Results Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = FuelAmberBg),
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
                            Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = FuelAmber, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "ESTIMATED TRIP COST",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FuelAmber,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FuelAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${df.format(totalDistance)} km total",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FuelAmber
                            )
                        }
                    }

                    Text(
                        text = "₹${df.format(totalCost)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF78350F)
                    )

                    if (passengerCount > 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Cost per Person: ₹${df.format(costPerPerson)} (split among $passengerCount)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    HorizontalDivider(color = FuelAmber.copy(alpha = 0.2f))

                    ResultValueRow(label = "Total Fuel Required", value = "${literDf.format(totalLiters)} Liters")
                    ResultValueRow(label = "Cost per Kilometer", value = "₹${df.format(costPerKm)} / km")
                    ResultValueRow(label = "Fuel Unit Rate", value = "₹${df.format(fuelPrice)} / L")

                    HorizontalDivider(color = FuelAmber.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "Trip Fuel Estimate:\nDistance: ${df.format(totalDistance)} km\nFuel: ${literDf.format(totalLiters)} L\nTotal Cost: ₹${df.format(totalCost)}" +
                                        if (passengerCount > 1) "\nShare ($passengerCount people): ₹${df.format(costPerPerson)} each" else ""
                                copyToClipboard(context, "Fuel Estimate", text)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FuelAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "Trip Fuel Planner:\nDistance: ${df.format(totalDistance)} km\nTotal Fuel: ${literDf.format(totalLiters)} L\nEstimated Cost: ₹${df.format(totalCost)}\nRate: ₹${df.format(costPerKm)}/km\n\nPlan with APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share Fuel Trip")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = FuelAmber, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = FuelAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
