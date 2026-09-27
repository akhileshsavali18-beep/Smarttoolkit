package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Height
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aistudio.smarttoolkit.tyvz.model.BmiCategory
import com.aistudio.smarttoolkit.tyvz.model.Calculators
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.BmiTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.BmiTealBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMetric by remember { mutableStateOf(true) }

    // Metric states
    var heightCmInput by remember { mutableStateOf("172") }
    var weightKgInput by remember { mutableStateOf("68") }

    // Imperial states
    var heightFeetInput by remember { mutableStateOf("5") }
    var heightInchesInput by remember { mutableStateOf("8") }
    var weightLbsInput by remember { mutableStateOf("150") }

    // Derived metric values for calculation
    val heightCm = if (isMetric) {
        heightCmInput.toDoubleOrNull() ?: 0.0
    } else {
        val ft = heightFeetInput.toDoubleOrNull() ?: 0.0
        val inches = heightInchesInput.toDoubleOrNull() ?: 0.0
        ((ft * 12.0) + inches) * 2.54
    }

    val weightKg = if (isMetric) {
        weightKgInput.toDoubleOrNull() ?: 0.0
    } else {
        val lbs = weightLbsInput.toDoubleOrNull() ?: 0.0
        lbs * 0.45359237
    }

    val bmiResult = remember(heightCm, weightKg) {
        Calculators.calculateBmi(heightCm, weightKg)
    }

    LaunchedEffect(bmiResult.bmi) {
        if (bmiResult.bmi > 0.0) {
            HapticUtils.performSuccess(context)
        }
    }

    val categoryColor = when (bmiResult.category) {
        BmiCategory.UNDERWEIGHT -> Color(0xFF0284C7) // Blue
        BmiCategory.NORMAL -> Color(0xFF16A34A)      // Green
        BmiCategory.OVERWEIGHT -> Color(0xFFEA580C)  // Orange
        BmiCategory.OBESE -> Color(0xFFDC2626)       // Red
    }

    val summaryText = """
        APS TOOLS - BMI & Health Report
        Height: ${if (isMetric) "$heightCmInput cm" else "$heightFeetInput ft $heightInchesInput in"}
        Weight: ${if (isMetric) "$weightKgInput kg" else "$weightLbsInput lbs"}
        BMI Score: ${bmiResult.bmi}
        Health Category: ${bmiResult.category.label}
        Ideal Weight Range: ${bmiResult.idealWeightMin} kg - ${bmiResult.idealWeightMax} kg
        Health Tip: ${bmiResult.category.advice}
    """.trimIndent()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "BMI & Health Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("bmi_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            if (isMetric) {
                                heightCmInput = "170"
                                weightKgInput = "65"
                            } else {
                                heightFeetInput = "5"
                                heightInchesInput = "7"
                                weightLbsInput = "143"
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = BmiTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unit Switcher (Metric cm/kg vs Imperial ft/lbs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isMetric) BmiTeal else Color.Transparent)
                        .clickable {
                            if (!isMetric) {
                                HapticUtils.performClick(context)
                                isMetric = true
                            }
                        }
                        .padding(vertical = 10.dp)
                        .testTag("bmi_unit_metric")
                ) {
                    Text(
                        text = "Metric (cm / kg)",
                        fontWeight = if (isMetric) FontWeight.Bold else FontWeight.Medium,
                        color = if (isMetric) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isMetric) BmiTeal else Color.Transparent)
                        .clickable {
                            if (isMetric) {
                                HapticUtils.performClick(context)
                                isMetric = false
                            }
                        }
                        .padding(vertical = 10.dp)
                        .testTag("bmi_unit_imperial")
                ) {
                    Text(
                        text = "Imperial (ft / lbs)",
                        fontWeight = if (!isMetric) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isMetric) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            // Input Fields Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Your Measurements",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isMetric) {
                        AppInputField(
                            value = heightCmInput,
                            onValueChange = { heightCmInput = it },
                            label = "Height",
                            suffixText = "cm",
                            leadingIcon = Icons.Default.Height,
                            testTag = "input_height_cm"
                        )

                        AppInputField(
                            value = weightKgInput,
                            onValueChange = { weightKgInput = it },
                            label = "Weight",
                            suffixText = "kg",
                            leadingIcon = Icons.Default.FitnessCenter,
                            testTag = "input_weight_kg"
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppInputField(
                                value = heightFeetInput,
                                onValueChange = { heightFeetInput = it },
                                label = "Feet",
                                suffixText = "ft",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f),
                                testTag = "input_height_ft"
                            )

                            AppInputField(
                                value = heightInchesInput,
                                onValueChange = { heightInchesInput = it },
                                label = "Inches",
                                suffixText = "in",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f),
                                testTag = "input_height_in"
                            )
                        }

                        AppInputField(
                            value = weightLbsInput,
                            onValueChange = { weightLbsInput = it },
                            label = "Weight",
                            suffixText = "lbs",
                            leadingIcon = Icons.Default.FitnessCenter,
                            testTag = "input_weight_lbs"
                        )
                    }
                }
            }

            // Results Card
            if (bmiResult.bmi > 0.0) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Your Body Mass Index",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${bmiResult.bmi}",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        color = categoryColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "kg/m²",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }

                            // Category pill badge
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(categoryColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = bmiResult.category.label,
                                    fontWeight = FontWeight.Bold,
                                    color = categoryColor,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Color-coded visual gauge bar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Box(modifier = Modifier.weight(18.5f).fillMaxSize().background(Color(0xFF0284C7)))
                                Box(modifier = Modifier.weight(6.4f).fillMaxSize().background(Color(0xFF16A34A)))
                                Box(modifier = Modifier.weight(5.0f).fillMaxSize().background(Color(0xFFEA580C)))
                                Box(modifier = Modifier.weight(10.0f).fillMaxSize().background(Color(0xFFDC2626)))
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Underweight\n<18.5", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Normal\n18.5-24.9", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Overweight\n25-29.9", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Obese\n30+", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        HorizontalDivider()

                        // Ideal Weight range
                        ResultValueRow(
                            label = "Healthy Weight Range for Height",
                            value = "${bmiResult.idealWeightMin} – ${bmiResult.idealWeightMax} kg",
                            isHighlighted = true,
                            highlightColor = Color(0xFF16A34A)
                        )

                        // Health Advice Tip
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(categoryColor.copy(alpha = 0.08f))
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = bmiResult.category.advice,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Action Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    copyToClipboard(context, "BMI Report", summaryText)
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy", fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    HapticUtils.performClick(context)
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, summaryText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(android.content.Intent.createChooser(sendIntent, "Share BMI Report"))
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BmiTeal),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
