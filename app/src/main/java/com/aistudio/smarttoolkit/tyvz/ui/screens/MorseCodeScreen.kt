package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.MorseCodeAmber
import com.aistudio.smarttoolkit.tyvz.ui.theme.MorseCodeAmberBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MorseMap {
    val charToMorse = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
        'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
        'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
        'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
        'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
        'Z' to "--..",
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----.",
        '.' to ".-.-.-", ',' to "--..--", '?' to "..--..", '/' to "-..-.",
        '-' to "-....-", '(' to "-.--.", ')' to "-.--.-", ' ' to "/"
    )

    val morseToChar = charToMorse.entries.associate { (k, v) -> v to k }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseCodeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Text->Morse, 1 = Morse->Text
    var inputText by remember { mutableStateOf("SOS MAYDAY") }
    var showReferenceChart by remember { mutableStateOf(false) }
    var isPlayingHaptic by remember { mutableStateOf(false) }

    val convertedResult by remember(inputText, selectedTab) {
        derivedStateOf {
            if (inputText.isBlank()) ""
            else if (selectedTab == 0) {
                // Text to Morse
                inputText.uppercase().map { ch ->
                    MorseMap.charToMorse[ch] ?: "?"
                }.joinToString(" ")
            } else {
                // Morse to Text
                val tokens = inputText.trim().split("\\s+".toRegex())
                tokens.map { token ->
                    if (token == "/") " "
                    else MorseMap.morseToChar[token]?.toString() ?: "?"
                }.joinToString("")
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_morse_code"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Morse Code Converter",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("morse_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText = ""
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MorseCodeAmberBg),
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
                            .background(MorseCodeAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "International Morse Translator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MorseCodeAmber
                            )
                        )
                        Text(
                            text = "Translate English text to standard dots/dashes and vice versa",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Mode Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        HapticUtils.performClick(context)
                        selectedTab = 0
                        inputText = "HELLO WORLD"
                    },
                    text = { Text("Text → Morse", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("morse_tab_text_to_morse")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        HapticUtils.performClick(context)
                        selectedTab = 1
                        inputText = "... --- ..."
                    },
                    text = { Text("Morse → Text", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("morse_tab_morse_to_text")
                )
            }

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = listOf("SOS", "HELLO", "APS TOOLS")
                presets.forEach { preset ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            HapticUtils.performClick(context)
                            if (selectedTab == 0) {
                                inputText = preset
                            } else {
                                inputText = preset.map { MorseMap.charToMorse[it] ?: "" }.joinToString(" ")
                            }
                        },
                        label = { Text(preset, fontSize = 11.sp) }
                    )
                }
            }

            // Input Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedTab == 0) "English Text Input" else "Morse Dots & Dashes (space-separated)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("morse_input_field"),
                    placeholder = {
                        Text(if (selectedTab == 0) "Type message..." else "Type ... --- ...")
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Quick Morse Keypad (Dots, Dashes, Space, Word Split)
            if (selectedTab == 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText += "."
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MorseCodeAmber)
                    ) {
                        Text("• Dot", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText += "-"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MorseCodeAmber)
                    ) {
                        Text("— Dash", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText += " "
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Space", fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText += " / "
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Word /", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Converted Result Card
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedTab == 0) "Morse Output" else "Decoded English Output",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("morse_result_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        if (convertedResult.isNotEmpty()) {
                            Text(
                                text = convertedResult,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    letterSpacing = if (selectedTab == 0) 2.sp else 0.sp
                                )
                            )
                        } else {
                            Text(
                                text = "Morse translation will appear here...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Action Buttons (Copy, Share, Play Haptic)
            if (convertedResult.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            copyToClipboard(context, convertedResult, "Morse Output")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("morse_copy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MorseCodeAmber),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            val morsePattern = if (selectedTab == 0) convertedResult else inputText
                            if (!isPlayingHaptic && morsePattern.isNotEmpty()) {
                                isPlayingHaptic = true
                                coroutineScope.launch {
                                    for (char in morsePattern) {
                                        if (char == '.') {
                                            HapticUtils.performClick(context)
                                            delay(120)
                                        } else if (char == '-') {
                                            HapticUtils.performSuccess(context)
                                            delay(280)
                                        } else if (char == ' ') {
                                            delay(150)
                                        }
                                    }
                                    isPlayingHaptic = false
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("morse_play_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPlayingHaptic) "Playing..." else "Pulse")
                    }

                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            ImageUtils.shareText(context, convertedResult, "Morse Code")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("morse_share_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                }
            }

            // Reference Chart Toggle
            OutlinedButton(
                onClick = {
                    HapticUtils.performClick(context)
                    showReferenceChart = !showReferenceChart
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (showReferenceChart) "Hide Morse Reference Table" else "View Morse Alphabet & Digits")
            }

            AnimatedVisibility(visible = showReferenceChart) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "International Morse Alphabet Reference",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MorseCodeAmber)
                        )
                        HorizontalDivider()
                        val alphabet = ('A'..'Z').toList()
                        alphabet.chunked(3).forEach { chunk ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                chunk.forEach { ch ->
                                    val code = MorseMap.charToMorse[ch].orEmpty()
                                    Text(
                                        text = "$ch: $code",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
