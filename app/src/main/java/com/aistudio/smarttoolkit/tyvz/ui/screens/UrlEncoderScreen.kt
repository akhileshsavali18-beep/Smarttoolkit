package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.filled.Link
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
import androidx.compose.material3.HorizontalDivider
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
import com.aistudio.smarttoolkit.tyvz.ui.theme.UrlTeal
import com.aistudio.smarttoolkit.tyvz.ui.theme.UrlTealBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlEncoderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Encode, 1 = Decode
    var inputText by remember { mutableStateOf("https://example.com/search?query=APS tools & accessories&category=offline") }
    var useRfc3986Percent20 by remember { mutableStateOf(true) }

    val result by remember(inputText, selectedTab, useRfc3986Percent20) {
        derivedStateOf {
            if (inputText.isBlank()) {
                Pair("", null)
            } else {
                try {
                    if (selectedTab == 0) {
                        var encoded = URLEncoder.encode(inputText, "UTF-8")
                        if (useRfc3986Percent20) {
                            encoded = encoded.replace("+", "%20")
                        }
                        Pair(encoded, null)
                    } else {
                        val decoded = URLDecoder.decode(inputText, "UTF-8")
                        Pair(decoded, null)
                    }
                } catch (e: Exception) {
                    Pair("", "URL error: ${e.localizedMessage ?: "Invalid encoding"}")
                }
            }
        }
    }

    // Parse query params if input looks like a URL
    val parsedParams = remember(inputText) {
        try {
            if (inputText.contains("?")) {
                val uri = Uri.parse(inputText)
                uri.queryParameterNames.map { key ->
                    Pair(key, uri.getQueryParameter(key) ?: "")
                }
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_url_encoder"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "URL Encoder / Decoder",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("url_back_button")
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
                colors = CardDefaults.cardColors(containerColor = UrlTealBg),
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
                            .background(UrlTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "URL Percent-Encoding Tool",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = UrlTeal
                            )
                        )
                        Text(
                            text = "Encode special characters or decode URL query strings safely",
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
                    text = { Text("Encode URL", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("url_tab_encode")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        HapticUtils.performClick(context)
                        selectedTab = 1
                    },
                    text = { Text("Decode URL", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("url_tab_decode")
                )
            }

            // Options Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = useRfc3986Percent20,
                    onClick = {
                        HapticUtils.performClick(context)
                        useRfc3986Percent20 = !useRfc3986Percent20
                    },
                    label = { Text(if (useRfc3986Percent20) "Space as %20 (RFC 3986)" else "Space as + (Form Url)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UrlTeal,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("url_chip_rfc")
                )

                IconButton(
                    onClick = {
                        HapticUtils.performClick(context)
                        if (result.first.isNotEmpty()) {
                            inputText = result.first
                            selectedTab = if (selectedTab == 0) 1 else 0
                        }
                    }
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Swap Input and Output", tint = UrlTeal)
                }
            }

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val samples = listOf(
                    "https://site.com/q?tag=tools & speed",
                    "User: John+Smith@work.org",
                    "%20Hello%20World%21"
                )
                samples.forEach { sample ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            HapticUtils.performClick(context)
                            inputText = sample
                            selectedTab = if (sample.startsWith("%")) 1 else 0
                        },
                        label = { Text(sample.take(20) + if (sample.length > 20) "..." else "", fontSize = 11.sp) }
                    )
                }
            }

            // Input Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedTab == 0) "Text / URL to Encode" else "Encoded URL to Decode",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("url_input_field"),
                    placeholder = { Text("Enter string or URL...") },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Output Card
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedTab == 0) "Encoded Output" else "Decoded Output",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("url_result_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.second != null) {
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
                        if (result.second != null) {
                            Text(
                                text = result.second.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else if (result.first.isNotEmpty()) {
                            Text(
                                text = result.first,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            )
                        } else {
                            Text(
                                text = "Converted URL will appear here...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Query Parameters Breakdown (if any)
            if (parsedParams.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Detected Query Parameters (${parsedParams.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = UrlTeal
                            )
                        )
                        HorizontalDivider()
                        parsedParams.forEach { (k, v) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = k,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = v,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            if (result.first.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performSuccess(context)
                            copyToClipboard(context, result.first, "URL Result")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("url_copy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = UrlTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Result")
                    }

                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            ImageUtils.shareText(context, result.first, "Share URL")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("url_share_button"),
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
