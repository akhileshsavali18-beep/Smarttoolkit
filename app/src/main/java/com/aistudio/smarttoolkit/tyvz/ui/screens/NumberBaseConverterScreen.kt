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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.NumberBasePurple
import com.aistudio.smarttoolkit.tyvz.ui.theme.NumberBasePurpleBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import java.math.BigInteger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberBaseConverterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var decimalStr by remember { mutableStateOf("255") }
    var binaryStr by remember { mutableStateOf("11111111") }
    var octalStr by remember { mutableStateOf("377") }
    var hexStr by remember { mutableStateOf("FF") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun updateFromDecimal(dec: String) {
        val clean = dec.trim()
        decimalStr = clean
        if (clean.isEmpty()) {
            binaryStr = ""
            octalStr = ""
            hexStr = ""
            errorMessage = null
            return
        }
        try {
            val num = BigInteger(clean, 10)
            binaryStr = num.toString(2)
            octalStr = num.toString(8)
            hexStr = num.toString(16).uppercase()
            errorMessage = null
        } catch (_: Exception) {
            errorMessage = "Invalid Decimal integer"
        }
    }

    fun updateFromBinary(bin: String) {
        val clean = bin.filter { it == '0' || it == '1' }
        binaryStr = clean
        if (clean.isEmpty()) {
            decimalStr = ""
            octalStr = ""
            hexStr = ""
            errorMessage = null
            return
        }
        try {
            val num = BigInteger(clean, 2)
            decimalStr = num.toString(10)
            octalStr = num.toString(8)
            hexStr = num.toString(16).uppercase()
            errorMessage = null
        } catch (_: Exception) {
            errorMessage = "Invalid Binary input"
        }
    }

    fun updateFromOctal(oct: String) {
        val clean = oct.filter { it in '0'..'7' }
        octalStr = clean
        if (clean.isEmpty()) {
            decimalStr = ""
            binaryStr = ""
            hexStr = ""
            errorMessage = null
            return
        }
        try {
            val num = BigInteger(clean, 8)
            decimalStr = num.toString(10)
            binaryStr = num.toString(2)
            hexStr = num.toString(16).uppercase()
            errorMessage = null
        } catch (_: Exception) {
            errorMessage = "Invalid Octal input"
        }
    }

    fun updateFromHex(hex: String) {
        val clean = hex.filter { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }.uppercase()
        hexStr = clean
        if (clean.isEmpty()) {
            decimalStr = ""
            binaryStr = ""
            octalStr = ""
            errorMessage = null
            return
        }
        try {
            val num = BigInteger(clean, 16)
            decimalStr = num.toString(10)
            binaryStr = num.toString(2)
            octalStr = num.toString(8)
            errorMessage = null
        } catch (_: Exception) {
            errorMessage = "Invalid Hexadecimal input"
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_number_base"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Number Base Converter",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("number_base_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            updateFromDecimal("")
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
                colors = CardDefaults.cardColors(containerColor = NumberBasePurpleBg),
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
                            .background(NumberBasePurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Base 2 / 8 / 10 / 16 Converter",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NumberBasePurple
                            )
                        )
                        Text(
                            text = "Real-time dynamic conversion across all common radix representations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = listOf("42", "255", "1024", "65535")
                presets.forEach { preset ->
                    FilterChip(
                        selected = decimalStr == preset,
                        onClick = {
                            HapticUtils.performClick(context)
                            updateFromDecimal(preset)
                        },
                        label = { Text("Dec $preset", fontSize = 11.sp) }
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Decimal Field (Base 10)
            BaseFieldItem(
                title = "Decimal (Base 10)",
                value = decimalStr,
                onValueChange = { updateFromDecimal(it) },
                keyboardType = KeyboardType.Number,
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, decimalStr, "Decimal Value")
                },
                testTag = "input_decimal"
            )

            // Binary Field (Base 2)
            val formattedBinary = remember(binaryStr) {
                if (binaryStr.length > 4) {
                    binaryStr.reversed().chunked(4).joinToString(" ").reversed()
                } else binaryStr
            }
            BaseFieldItem(
                title = "Binary (Base 2) [Bit length: ${binaryStr.length}]",
                value = binaryStr,
                onValueChange = { updateFromBinary(it) },
                keyboardType = KeyboardType.Number,
                subText = if (formattedBinary != binaryStr) "Grouped: $formattedBinary" else null,
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, binaryStr, "Binary Value")
                },
                testTag = "input_binary"
            )

            // Hexadecimal Field (Base 16)
            BaseFieldItem(
                title = "Hexadecimal (Base 16)",
                value = hexStr,
                onValueChange = { updateFromHex(it) },
                keyboardType = KeyboardType.Text,
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, hexStr, "Hex Value")
                },
                testTag = "input_hex"
            )

            // Quick Hex Keypad Row (A - F)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("A", "B", "C", "D", "E", "F").forEach { letter ->
                    OutlinedButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            updateFromHex(hexStr + letter)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(letter, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Octal Field (Base 8)
            BaseFieldItem(
                title = "Octal (Base 8)",
                value = octalStr,
                onValueChange = { updateFromOctal(it) },
                keyboardType = KeyboardType.Number,
                onCopy = {
                    HapticUtils.performSuccess(context)
                    copyToClipboard(context, octalStr, "Octal Value")
                },
                testTag = "input_octal"
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BaseFieldItem(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    subText: String? = null,
    onCopy: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NumberBasePurple)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(testTag),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy $title")
                }
            }
            if (subText != null) {
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
