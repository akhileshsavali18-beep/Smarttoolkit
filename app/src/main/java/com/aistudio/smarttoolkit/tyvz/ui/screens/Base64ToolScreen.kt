package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.util.Base64
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.Base64Slate
import com.aistudio.smarttoolkit.tyvz.ui.theme.Base64SlateBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Base64ToolScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Encode, 1 = Decode
    var inputText by remember { mutableStateOf("APS Tools - Offline Utility Suite") }
    var urlSafeMode by remember { mutableStateOf(false) }

    val conversionResult by remember(inputText, selectedTab, urlSafeMode) {
        derivedStateOf {
            if (inputText.isBlank()) {
                Pair("", null)
            } else {
                try {
                    val flags = if (urlSafeMode) {
                        Base64.URL_SAFE or Base64.NO_WRAP
                    } else {
                        Base64.NO_WRAP
                    }

                    if (selectedTab == 0) {
                        // Encode
                        val bytes = inputText.toByteArray(Charsets.UTF_8)
                        val encoded = Base64.encodeToString(bytes, flags)
                        Pair(encoded, null)
                    } else {
                        // Decode
                        val decodedBytes = Base64.decode(inputText.trim(), flags)
                        val decoded = String(decodedBytes, Charsets.UTF_8)
                        Pair(decoded, null)
                    }
                } catch (e: Exception) {
                    Pair("", "Invalid Base64 input: ${e.localizedMessage ?: "Decoding error"}")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_base64"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Base64 Encoder / Decoder",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("base64_back_button")
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
                colors = CardDefaults.cardColors(containerColor = Base64SlateBg),
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
                            .background(Base64Slate),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Base64 Text Utility",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Base64Slate
                            )
                        )
                        Text(
                            text = "Convert text to Base64 standard or URL-safe representation offline",
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
                    },
                    text = { Text("Encode (Text → Base64)", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("base64_tab_encode")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        HapticUtils.performClick(context)
                        selectedTab = 1
                    },
                    text = { Text("Decode (Base64 → Text)", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("base64_tab_decode")
                )
            }

            // Options Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = urlSafeMode,
                    onClick = {
                        HapticUtils.performClick(context)
                        urlSafeMode = !urlSafeMode
                    },
                    label = { Text("URL-Safe Mode (- and _)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Base64Slate,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("base64_chip_url_safe")
                )

                IconButton(
                    onClick = {
                        HapticUtils.performClick(context)
                        if (conversionResult.first.isNotEmpty()) {
                            inputText = conversionResult.first
                            selectedTab = if (selectedTab == 0) 1 else 0
                        }
                    }
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Swap Input and Output", tint = Base64Slate)
                }
            }

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sampleTexts = listOf("Hello World!", "admin:secret123", "{\"id\":101}")
                sampleTexts.forEach { sample ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText = sample
                            selectedTab = 0
                        },
                        label = { Text(sample, maxLines = 1, fontSize = 11.sp) }
                    )
                }
            }

            // Input Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedTab == 0) "Plain Text Input" else "Base64 Input",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "${inputText.length} chars | ${inputText.toByteArray(Charsets.UTF_8).size} bytes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("base64_input_field"),
                    placeholder = {
                        Text(
                            if (selectedTab == 0) "Enter plain text to encode..."
                            else "Paste Base64 encoded string to decode..."
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Output / Result Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTab == 0) "Base64 Output" else "Decoded Plain Text",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (conversionResult.first.isNotEmpty()) {
                        Text(
                            text = "${conversionResult.first.length} chars",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("base64_result_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (conversionResult.second != null) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        if (conversionResult.second != null) {
                            Text(
                                text = conversionResult.second.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else if (conversionResult.first.isNotEmpty()) {
                            Text(
                                text = conversionResult.first,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            )
                        } else {
                            Text(
                                text = "Output will appear here automatically...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Action Buttons
            if (conversionResult.first.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            copyToClipboard(context, conversionResult.first, "Base64 Result")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("base64_copy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Base64Slate),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Result")
                    }

                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            ImageUtils.shareText(context, conversionResult.first, "Share Base64 Output")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("base64_share_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
