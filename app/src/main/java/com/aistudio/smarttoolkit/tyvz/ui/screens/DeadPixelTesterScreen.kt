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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.theme.DeadPixelDark
import com.aistudio.smarttoolkit.tyvz.ui.theme.DeadPixelDarkBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadPixelTesterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFullScreenMode by remember { mutableStateOf(false) }
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    val testColors = listOf(
        Pair("Pure Red", Color.Red),
        Pair("Pure Green", Color(0xFF00FF00)),
        Pair("Pure Blue", Color.Blue),
        Pair("Pure White", Color.White),
        Pair("Pure Black", Color.Black)
    )

    if (isFullScreenMode) {
        // Fullscreen test surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(testColors[selectedColorIndex].second)
                .clickable {
                    HapticUtils.performClick(context)
                    // Cycle to next color or exit if on last color
                    if (selectedColorIndex < testColors.size - 1) {
                        selectedColorIndex++
                    } else {
                        selectedColorIndex = 0
                        isFullScreenMode = false
                    }
                }
                .testTag("dead_pixel_fullscreen_box")
        ) {
            // Tiny Exit Button in top-right corner
            IconButton(
                onClick = {
                    isFullScreenMode = false
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit Fullscreen", tint = Color.White)
            }

            Text(
                text = "Tap anywhere to switch color (${testColors[selectedColorIndex].first})",
                color = if (testColors[selectedColorIndex].second == Color.White) Color.Black else Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    } else {
        Scaffold(
            modifier = modifier.testTag("screen_dead_pixel_tester"),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Dead Pixel / Color Tester",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("dead_pixel_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeadPixelDarkBg),
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
                                .background(DeadPixelDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Display Defect & Pixel Inspection",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = DeadPixelDark
                            )
                            Text(
                                text = "Spot stuck pixels, dead OLED sub-pixels, and backlight bleeding",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Palette Tiles Selector
                Text("Select Test Color", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    testColors.forEachIndexed { idx, (name, col) ->
                        FilterChip(
                            selected = selectedColorIndex == idx,
                            onClick = {
                                HapticUtils.performClick(context)
                                selectedColorIndex = idx
                            },
                            label = { Text(name, fontSize = 11.sp) }
                        )
                    }
                }

                // Color Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(testColors[selectedColorIndex].second),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = testColors[selectedColorIndex].first,
                        color = if (testColors[selectedColorIndex].second == Color.White) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Launch Fullscreen Button
                Button(
                    onClick = {
                        HapticUtils.performClick(context)
                        isFullScreenMode = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("button_start_dead_pixel_test"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Fullscreen Test", fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "Tip: When fullscreen mode begins, gently inspect the screen for any miscolored dots. Tap anywhere to cycle between Red, Green, Blue, White, and Black.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
