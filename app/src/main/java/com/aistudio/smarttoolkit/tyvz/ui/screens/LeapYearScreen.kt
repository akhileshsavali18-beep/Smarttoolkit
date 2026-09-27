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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.LeapYearGreen
import com.aistudio.smarttoolkit.tyvz.ui.theme.LeapYearGreenBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

private fun checkLeapYear(year: Int): Pair<Boolean, String> {
    return if (year % 400 == 0) {
        Pair(true, "$year is divisible by 400, making it a leap year.")
    } else if (year % 100 == 0) {
        Pair(false, "$year is divisible by 100 but NOT 400, so it is a common year (not a leap year).")
    } else if (year % 4 == 0) {
        Pair(true, "$year is divisible by 4 and not a century year, making it a leap year (366 days, Feb has 29 days).")
    } else {
        Pair(false, "$year is not divisible by 4, so it is a regular 365-day year.")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeapYearScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var yearInput by remember { mutableStateOf("2028") }

    val yearVal = yearInput.toIntOrNull()
    val leapResult = remember(yearVal) {
        if (yearVal != null && yearVal > 0) {
            checkLeapYear(yearVal)
        } else {
            Pair(false, "Please enter a valid positive year.")
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_leap_year"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Leap Year Checker",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("leap_year_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            yearInput = "2028"
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = LeapYearGreenBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LeapYearGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gregorian Leap Year Engine",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LeapYearGreen
                        )
                        Text(
                            text = "Check whether any year has 366 days and read the rule explanation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Year Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2024, 2025, 2026, 2028, 2030, 2100).forEach { y ->
                    FilterChip(
                        selected = yearInput == "$y",
                        onClick = {
                            HapticUtils.performClick(context)
                            yearInput = "$y"
                        },
                        label = { Text("$y", fontSize = 12.sp) }
                    )
                }
            }

            // Year Input Field
            OutlinedTextField(
                value = yearInput,
                onValueChange = { yearInput = it.filter { ch -> ch.isDigit() } },
                label = { Text("Year (AD/CE)") },
                placeholder = { Text("e.g. 2028, 2024...") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_leap_year"),
                shape = RoundedCornerShape(12.dp)
            )

            // Result Display Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (leapResult.first) LeapYearGreenBg else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (leapResult.first) Icons.Default.CheckCircle else Icons.Default.HighlightOff,
                            contentDescription = null,
                            tint = if (leapResult.first) LeapYearGreen else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = if (leapResult.first) "LEAP YEAR (366 Days)" else "COMMON YEAR (365 Days)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (leapResult.first) LeapYearGreen else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Text(
                        text = leapResult.second,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
