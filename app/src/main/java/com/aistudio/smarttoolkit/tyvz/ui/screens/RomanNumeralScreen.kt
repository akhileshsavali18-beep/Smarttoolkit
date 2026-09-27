package com.aistudio.smarttoolkit.tyvz.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatListNumbered
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.RomanGold
import com.aistudio.smarttoolkit.tyvz.ui.theme.RomanGoldBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils

enum class RomanMode(val title: String) {
    NUMBER_TO_ROMAN("Number to Roman"),
    ROMAN_TO_NUMBER("Roman to Number")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomanNumeralScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(RomanMode.NUMBER_TO_ROMAN) }
    var numberInput by remember { mutableStateOf("2026") }
    var romanInput by remember { mutableStateOf("MMXXVI") }

    val romanNumeralsMap = remember {
        listOf(
            1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
            100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
            10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
        )
    }

    // Number to Roman conversion
    val convertedToRoman = remember(numberInput) {
        val num = numberInput.toIntOrNull()
        if (num == null || num <= 0 || num > 3999) {
            "Enter integer between 1 and 3999"
        } else {
            var temp = num
            val sb = StringBuilder()
            for ((value, roman) in romanNumeralsMap) {
                while (temp >= value) {
                    sb.append(roman)
                    temp -= value
                }
            }
            sb.toString()
        }
    }

    // Roman to Number conversion
    val convertedToNumber = remember(romanInput) {
        val cleaned = romanInput.trim().uppercase()
        if (cleaned.isEmpty()) {
            "Enter Roman numeral"
        } else {
            val romanValues = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
            var sum = 0
            var prev = 0
            var isValid = true

            for (i in cleaned.length - 1 downTo 0) {
                val current = romanValues[cleaned[i]]
                if (current == null) {
                    isValid = false
                    break
                }
                if (current < prev) {
                    sum -= current
                } else {
                    sum += current
                }
                prev = current
            }

            if (isValid && sum in 1..3999) {
                sum.toString()
            } else {
                "Invalid Roman numeral"
            }
        }
    }

    val displayResult = if (selectedMode == RomanMode.NUMBER_TO_ROMAN) convertedToRoman else convertedToNumber

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Roman Numeral Converter",
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
                        numberInput = "2026"
                        romanInput = "MMXXVI"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("roman_numeral_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RomanMode.values().forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = {
                            HapticUtils.performClick(context)
                            selectedMode = mode
                        },
                        label = { Text(mode.title, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RomanGold,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

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
                    if (selectedMode == RomanMode.NUMBER_TO_ROMAN) {
                        AppInputField(
                            value = numberInput,
                            onValueChange = { numberInput = it },
                            label = "Enter Standard Number (1 – 3999)",
                            placeholder = "e.g. 2026",
                            testTag = "input_arabic_number"
                        )
                    } else {
                        AppInputField(
                            value = romanInput,
                            onValueChange = { romanInput = it.uppercase() },
                            label = "Enter Roman Numeral",
                            placeholder = "e.g. MMXXVI",
                            testTag = "input_roman_string"
                        )

                        // Quick Roman keypad
                        Text(
                            text = "Quick Roman Keypad",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("I", "V", "X", "L", "C", "D", "M").forEach { char ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable {
                                            HapticUtils.performClick(context)
                                            romanInput += char
                                        }
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(char, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        if (romanInput.isNotEmpty()) {
                                            HapticUtils.performClick(context)
                                            romanInput = romanInput.dropLast(1)
                                        }
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.Backspace, contentDescription = "Backspace", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Results Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = RomanGoldBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = RomanGold, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (selectedMode == RomanMode.NUMBER_TO_ROMAN) "ROMAN NUMERAL" else "STANDARD NUMBER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanGold,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RomanGold.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedMode.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanGold
                            )
                        }
                    }

                    Text(
                        text = displayResult,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF78350F)
                    )

                    HorizontalDivider(color = RomanGold.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                copyToClipboard(context, "Roman Numeral", displayResult)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RomanGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy Result", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "Roman Numeral Conversion:\nResult: $displayResult\n\nConverted via APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share Conversion")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = RomanGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = RomanGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Reference Chart
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ROMAN REFERENCE CHART",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("I = 1", fontWeight = FontWeight.Bold)
                        Text("V = 5", fontWeight = FontWeight.Bold)
                        Text("X = 10", fontWeight = FontWeight.Bold)
                        Text("L = 50", fontWeight = FontWeight.Bold)
                        Text("C = 100", fontWeight = FontWeight.Bold)
                        Text("D = 500", fontWeight = FontWeight.Bold)
                        Text("M = 1000", fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "Subtractive Combinations:\nIV = 4, IX = 9, XL = 40, XC = 90, CD = 400, CM = 900",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
