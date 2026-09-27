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
import androidx.compose.material.icons.filled.FitnessCenter
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.IdealWeightViolet
import com.aistudio.smarttoolkit.tyvz.ui.theme.IdealWeightVioletBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat

enum class Gender(val title: String) {
    MALE("Male"),
    FEMALE("Female")
}

enum class BodyFrame(val title: String, val factor: Double) {
    SMALL("Small Frame", 0.95),
    MEDIUM("Medium Frame", 1.00),
    LARGE("Large Frame", 1.05)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdealWeightScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var heightCmInput by remember { mutableStateOf("175") }
    var selectedFrame by remember { mutableStateOf(BodyFrame.MEDIUM) }

    val df = remember { DecimalFormat("#,##0.1") }

    val heightCm = heightCmInput.toDoubleOrNull() ?: 175.0
    val heightInches = heightCm / 2.54
    val inchesOver5Feet = (heightInches - 60.0).coerceAtLeast(0.0)

    // Devine formula:
    // Male: 50 kg + 2.3 kg per inch over 5 feet
    // Female: 45.5 kg + 2.3 kg per inch over 5 feet
    val devineBase = if (selectedGender == Gender.MALE) {
        50.0 + (2.3 * inchesOver5Feet)
    } else {
        45.5 + (2.3 * inchesOver5Feet)
    }

    // Robinson formula (1983):
    // Male: 52 kg + 1.9 kg per inch over 5 feet
    // Female: 49 kg + 1.7 kg per inch over 5 feet
    val robinsonBase = if (selectedGender == Gender.MALE) {
        52.0 + (1.9 * inchesOver5Feet)
    } else {
        49.0 + (1.7 * inchesOver5Feet)
    }

    // Miller formula:
    // Male: 56.2 kg + 1.41 kg per inch over 5 feet
    // Female: 53.1 kg + 1.36 kg per inch over 5 feet
    val millerBase = if (selectedGender == Gender.MALE) {
        56.2 + (1.41 * inchesOver5Feet)
    } else {
        53.1 + (1.36 * inchesOver5Feet)
    }

    // Adjusted for body frame
    val adjustedDevine = devineBase * selectedFrame.factor
    val adjustedRobinson = robinsonBase * selectedFrame.factor
    val adjustedMiller = millerBase * selectedFrame.factor

    val averageIdealKg = (adjustedDevine + adjustedRobinson + adjustedMiller) / 3.0
    val averageIdealLbs = averageIdealKg * 2.20462

    val healthyMinKg = averageIdealKg * 0.90
    val healthyMaxKg = averageIdealKg * 1.10

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ideal Body Weight",
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
                        selectedGender = Gender.MALE
                        heightCmInput = "175"
                        selectedFrame = BodyFrame.MEDIUM
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("ideal_weight_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Inputs Card
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
                        text = "BODY PARAMETERS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    // Gender selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Gender.values().forEach { g ->
                            FilterChip(
                                selected = selectedGender == g,
                                onClick = {
                                    HapticUtils.performClick(context)
                                    selectedGender = g
                                },
                                label = { Text(g.title, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IdealWeightViolet,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    AppInputField(
                        value = heightCmInput,
                        onValueChange = { heightCmInput = it },
                        label = "Height (cm)",
                        suffixText = "cm",
                        placeholder = "e.g. 175",
                        testTag = "input_height_cm"
                    )

                    // Frame size chips
                    Text(
                        text = "Body Frame Size",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BodyFrame.values().forEach { frame ->
                            FilterChip(
                                selected = selectedFrame == frame,
                                onClick = {
                                    HapticUtils.performClick(context)
                                    selectedFrame = frame
                                },
                                label = { Text(frame.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IdealWeightViolet,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Results Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = IdealWeightVioletBg),
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
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = IdealWeightViolet, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "IDEAL BODY WEIGHT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IdealWeightViolet,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(IdealWeightViolet.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${selectedGender.title} • ${selectedFrame.title}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IdealWeightViolet
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${df.format(averageIdealKg)} kg",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4C1D95)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "(${df.format(averageIdealLbs)} lbs)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6D28D9),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Healthy Range: ${df.format(healthyMinKg)} kg – ${df.format(healthyMaxKg)} kg",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5B21B6)
                        )
                    }

                    HorizontalDivider(color = IdealWeightViolet.copy(alpha = 0.2f))

                    ResultValueRow(label = "Devine Formula (Clinical Gold)", value = "${df.format(adjustedDevine)} kg")
                    ResultValueRow(label = "Robinson Formula (1983)", value = "${df.format(adjustedRobinson)} kg")
                    ResultValueRow(label = "Miller Formula", value = "${df.format(adjustedMiller)} kg")

                    HorizontalDivider(color = IdealWeightViolet.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "Ideal Body Weight:\nHeight: ${heightCm}cm (${selectedGender.title})\nIdeal Weight: ${df.format(averageIdealKg)} kg (${df.format(averageIdealLbs)} lbs)\nHealthy Range: ${df.format(healthyMinKg)} - ${df.format(healthyMaxKg)} kg"
                                copyToClipboard(context, "Ideal Weight", text)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IdealWeightViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "My Ideal Weight Calculation:\nHeight: ${heightCm} cm (${selectedGender.title})\nRecommended: ${df.format(averageIdealKg)} kg\nRange: ${df.format(healthyMinKg)} - ${df.format(healthyMaxKg)} kg\n\nCalculated via APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share Weight")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = IdealWeightViolet, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = IdealWeightViolet, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
