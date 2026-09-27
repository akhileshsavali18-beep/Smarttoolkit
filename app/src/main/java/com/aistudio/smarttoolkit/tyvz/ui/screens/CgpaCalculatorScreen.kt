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
import androidx.compose.material.icons.filled.School
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.CgpaIndigo
import com.aistudio.smarttoolkit.tyvz.ui.theme.CgpaIndigoBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cgpaInput by remember { mutableStateOf("8.5") }
    var multiplierInput by remember { mutableStateOf("9.5") }
    var selectedPreset by remember { mutableStateOf("9.5 (CBSE/VTU)") }

    val df = remember { DecimalFormat("#,##0.00") }

    val cgpa = cgpaInput.toDoubleOrNull() ?: 0.0
    val multiplier = multiplierInput.toDoubleOrNull() ?: 9.5
    val percentage = (cgpa * multiplier).coerceIn(0.0, 100.0)

    val gradeClassification = when {
        percentage >= 75.0 -> "First Class with Distinction (Grade O/A+)"
        percentage >= 60.0 -> "First Class (Grade A)"
        percentage >= 50.0 -> "Second Class (Grade B)"
        percentage >= 40.0 -> "Pass Class (Grade C)"
        else -> "Needs Improvement"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "CGPA to Percentage",
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
                        cgpaInput = "8.0"
                        multiplierInput = "9.5"
                        selectedPreset = "9.5 (CBSE/VTU)"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("cgpa_calc_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Card
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
                    AppInputField(
                        value = cgpaInput,
                        onValueChange = { cgpaInput = it },
                        label = "Enter CGPA / GPA (out of 10.0)",
                        placeholder = "e.g. 8.5",
                        testTag = "input_cgpa"
                    )

                    Text(
                        text = "University Multiplier Scale",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("9.5 (CBSE/VTU)", "10.0 (Direct)", "Custom").forEach { preset ->
                            FilterChip(
                                selected = selectedPreset == preset,
                                onClick = {
                                    HapticUtils.performClick(context)
                                    selectedPreset = preset
                                    when (preset) {
                                        "9.5 (CBSE/VTU)" -> multiplierInput = "9.5"
                                        "10.0 (Direct)" -> multiplierInput = "10.0"
                                    }
                                },
                                label = { Text(preset, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CgpaIndigo,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (selectedPreset == "Custom") {
                        AppInputField(
                            value = multiplierInput,
                            onValueChange = { multiplierInput = it },
                            label = "Custom Multiplier Formula Factor",
                            placeholder = "e.g. 7.1 or 9.5",
                            testTag = "input_multiplier"
                        )
                    }
                }
            }

            // Results Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CgpaIndigoBg),
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
                            Icon(Icons.Default.School, contentDescription = null, tint = CgpaIndigo, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "EQUIVALENT PERCENTAGE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CgpaIndigo,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CgpaIndigo.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Scale: ×$multiplierInput",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CgpaIndigo
                            )
                        }
                    }

                    Text(
                        text = "${df.format(percentage)}%",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CgpaIndigo
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Result Division: $gradeClassification",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E1B4B)
                        )
                    }

                    HorizontalDivider(color = CgpaIndigo.copy(alpha = 0.2f))

                    ResultValueRow(label = "Input CGPA", value = "$cgpaInput / 10.0")
                    ResultValueRow(label = "Formula Used", value = "$cgpaInput × $multiplierInput")
                    ResultValueRow(label = "Converted Percentage", value = "${df.format(percentage)}%")

                    HorizontalDivider(color = CgpaIndigo.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "CGPA to Percentage:\nCGPA: $cgpaInput (Scale ×$multiplierInput)\nPercentage: ${df.format(percentage)}%\nDivision: $gradeClassification"
                                copyToClipboard(context, "CGPA Result", text)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CgpaIndigo),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy Result", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "Academic Conversion:\nCGPA: $cgpaInput\nPercentage: ${df.format(percentage)}%\n$gradeClassification\n\nCalculated via APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share CGPA")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = CgpaIndigo, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = CgpaIndigo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
