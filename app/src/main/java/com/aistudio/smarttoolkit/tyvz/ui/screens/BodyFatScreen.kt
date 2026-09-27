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
import androidx.compose.material.icons.filled.MonitorWeight
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.BodyFatRose
import com.aistudio.smarttoolkit.tyvz.ui.theme.BodyFatRoseBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat
import kotlin.math.log10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyFatScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMale by remember { mutableStateOf(true) }
    var heightCmInput by remember { mutableStateOf("175") }
    var weightKgInput by remember { mutableStateOf("72") }
    var waistCmInput by remember { mutableStateOf("84") }
    var neckCmInput by remember { mutableStateOf("38") }
    var hipCmInput by remember { mutableStateOf("95") } // for females

    val df = remember { DecimalFormat("#,##0.1") }

    val height = heightCmInput.toDoubleOrNull()?.coerceAtLeast(50.0) ?: 175.0
    val weight = weightKgInput.toDoubleOrNull()?.coerceAtLeast(30.0) ?: 72.0
    val waist = waistCmInput.toDoubleOrNull()?.coerceAtLeast(30.0) ?: 84.0
    val neck = neckCmInput.toDoubleOrNull()?.coerceAtLeast(20.0) ?: 38.0
    val hip = hipCmInput.toDoubleOrNull()?.coerceAtLeast(30.0) ?: 95.0

    // U.S. Navy Body Fat formula
    val bodyFatPct = remember(isMale, height, weight, waist, neck, hip) {
        val calculated = if (isMale) {
            val waistNeckDiff = (waist - neck).coerceAtLeast(1.0)
            495.0 / (1.0324 - 0.19077 * log10(waistNeckDiff) + 0.15456 * log10(height)) - 450.0
        } else {
            val waistHipNeckDiff = (waist + hip - neck).coerceAtLeast(1.0)
            495.0 / (1.29579 - 0.35004 * log10(waistHipNeckDiff) + 0.22100 * log10(height)) - 450.0
        }
        calculated.coerceIn(3.0, 60.0)
    }

    val fatMassKg = weight * (bodyFatPct / 100.0)
    val leanMassKg = weight - fatMassKg

    val category = when {
        isMale -> when {
            bodyFatPct < 6.0 -> "Essential Fat"
            bodyFatPct <= 13.0 -> "Athletes Level"
            bodyFatPct <= 17.0 -> "Fitness Category"
            bodyFatPct <= 24.0 -> "Average Healthy"
            else -> "High / Obese"
        }
        else -> when {
            bodyFatPct < 14.0 -> "Essential Fat"
            bodyFatPct <= 20.0 -> "Athletes Level"
            bodyFatPct <= 24.0 -> "Fitness Category"
            bodyFatPct <= 31.0 -> "Average Healthy"
            else -> "High / Obese"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Body Fat Estimator",
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
                        isMale = true
                        heightCmInput = "175"
                        weightKgInput = "72"
                        waistCmInput = "84"
                        neckCmInput = "38"
                        hipCmInput = "95"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("body_fat_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Measurements Card
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
                        text = "CIRCUMFERENCE MEASUREMENTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = isMale,
                            onClick = {
                                HapticUtils.performClick(context)
                                isMale = true
                            },
                            label = { Text("Male", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BodyFatRose,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = !isMale,
                            onClick = {
                                HapticUtils.performClick(context)
                                isMale = false
                            },
                            label = { Text("Female", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BodyFatRose,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AppInputField(
                        value = heightCmInput,
                        onValueChange = { heightCmInput = it },
                        label = "Height",
                        suffixText = "cm",
                        placeholder = "e.g. 175",
                        testTag = "input_fat_height"
                    )

                    AppInputField(
                        value = weightKgInput,
                        onValueChange = { weightKgInput = it },
                        label = "Current Weight",
                        suffixText = "kg",
                        placeholder = "e.g. 72",
                        testTag = "input_fat_weight"
                    )

                    AppInputField(
                        value = waistCmInput,
                        onValueChange = { waistCmInput = it },
                        label = "Waist Circumference (at navel)",
                        suffixText = "cm",
                        placeholder = "e.g. 84",
                        testTag = "input_fat_waist"
                    )

                    AppInputField(
                        value = neckCmInput,
                        onValueChange = { neckCmInput = it },
                        label = "Neck Circumference (below larynx)",
                        suffixText = "cm",
                        placeholder = "e.g. 38",
                        testTag = "input_fat_neck"
                    )

                    if (!isMale) {
                        AppInputField(
                            value = hipCmInput,
                            onValueChange = { hipCmInput = it },
                            label = "Hip Circumference (widest point)",
                            suffixText = "cm",
                            placeholder = "e.g. 95",
                            testTag = "input_fat_hip"
                        )
                    }
                }
            }

            // Results Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = BodyFatRoseBg),
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
                            Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = BodyFatRose, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "ESTIMATED BODY FAT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BodyFatRose,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BodyFatRose.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BodyFatRose
                            )
                        }
                    }

                    Text(
                        text = "${df.format(bodyFatPct)}%",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF881337)
                    )

                    HorizontalDivider(color = BodyFatRose.copy(alpha = 0.2f))

                    ResultValueRow(label = "Fat Mass (Adipose)", value = "${df.format(fatMassKg)} kg")
                    ResultValueRow(label = "Lean Body Mass (Muscle & Bone)", value = "${df.format(leanMassKg)} kg")
                    ResultValueRow(label = "Health Category", value = category)
                    ResultValueRow(label = "Formula Reference", value = "U.S. Navy Circumference Standard")

                    HorizontalDivider(color = BodyFatRose.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "Body Fat Estimation:\nGender: ${if (isMale) "Male" else "Female"}\nBody Fat: ${df.format(bodyFatPct)}% ($category)\nFat Mass: ${df.format(fatMassKg)} kg\nLean Mass: ${df.format(leanMassKg)} kg"
                                copyToClipboard(context, "Body Fat Result", text)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BodyFatRose),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "My Body Composition:\nEstimated Body Fat: ${df.format(bodyFatPct)}%\nStatus: $category\nLean Body Mass: ${df.format(leanMassKg)} kg\n\nEstimated via APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share Body Fat")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = BodyFatRose, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = BodyFatRose, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
