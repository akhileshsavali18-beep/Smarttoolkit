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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.DuplicateCyan
import com.aistudio.smarttoolkit.tyvz.ui.theme.DuplicateCyanBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateRemoverScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember {
        mutableStateOf("Apple\nBanana\nOrange\nApple\nMango\nBanana\nGrape")
    }
    var modeByLines by remember { mutableStateOf(true) } // true: Lines, false: Words
    var caseSensitive by remember { mutableStateOf(false) }
    var trimSpaces by remember { mutableStateOf(true) }

    val cleanedResult = remember(inputText, modeByLines, caseSensitive, trimSpaces) {
        if (inputText.isBlank()) return@remember ""

        if (modeByLines) {
            val lines = inputText.split("\n")
            val seen = mutableSetOf<String>()
            val resultList = mutableListOf<String>()
            for (line in lines) {
                val processed = if (trimSpaces) line.trim() else line
                val checkKey = if (caseSensitive) processed else processed.lowercase()
                if (processed.isNotEmpty() && !seen.contains(checkKey)) {
                    seen.add(checkKey)
                    resultList.add(processed)
                }
            }
            resultList.joinToString("\n")
        } else {
            val words = inputText.split("\\s+".toRegex())
            val seen = mutableSetOf<String>()
            val resultList = mutableListOf<String>()
            for (w in words) {
                val checkKey = if (caseSensitive) w else w.lowercase()
                if (w.isNotEmpty() && !seen.contains(checkKey)) {
                    seen.add(checkKey)
                    resultList.add(w)
                }
            }
            resultList.joinToString(" ")
        }
    }

    val removedCount = remember(inputText, cleanedResult, modeByLines) {
        if (inputText.isBlank()) 0
        else {
            val originalTotal = if (modeByLines) inputText.split("\n").count { it.isNotBlank() }
            else inputText.split("\\s+".toRegex()).count { it.isNotBlank() }
            val cleanedTotal = if (modeByLines) cleanedResult.split("\n").count { it.isNotBlank() }
            else cleanedResult.split("\\s+".toRegex()).count { it.isNotBlank() }
            maxOf(0, originalTotal - cleanedTotal)
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_duplicate_remover"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Duplicate Line & Word Remover",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("duplicate_remover_back_button")
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
                colors = CardDefaults.cardColors(containerColor = DuplicateCyanBg),
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
                            .background(DuplicateCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Deduplicate Text & Lists",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = DuplicateCyan
                        )
                        Text(
                            text = "Clean repetitive entries, phone numbers, or keywords in one tap",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = modeByLines,
                    onClick = {
                        HapticUtils.performClick(context)
                        modeByLines = true
                    },
                    label = { Text("Deduplicate Lines") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !modeByLines,
                    onClick = {
                        HapticUtils.performClick(context)
                        modeByLines = false
                    },
                    label = { Text("Deduplicate Words") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Option Checkboxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = trimSpaces,
                        onCheckedChange = { trimSpaces = it }
                    )
                    Text("Trim Whitespace", fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = caseSensitive,
                        onCheckedChange = { caseSensitive = it }
                    )
                    Text("Case Sensitive", fontSize = 13.sp)
                }
            }

            // Input Field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Input Text with Duplicates") },
                placeholder = { Text("Paste lists or lines here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_duplicate_text"),
                minLines = 4,
                maxLines = 8,
                shape = RoundedCornerShape(12.dp)
            )

            // Result Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cleaned Output ($removedCount duplicates removed)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                if (cleanedResult.isNotBlank()) {
                                    copyToClipboard(context, cleanedResult, "Cleaned text copied")
                                }
                            },
                            modifier = Modifier.testTag("button_copy_cleaned_text")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy")
                        }
                    }

                    Text(
                        text = cleanedResult.ifEmpty { "Result will appear here..." },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
