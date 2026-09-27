package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.ui.theme.ApsBlue
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.io.File
import java.io.FileOutputStream

data class DrawPathItem(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

enum class BoardMode {
    WHITEBOARD,
    SIGNATURE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentMode by remember { mutableStateOf(BoardMode.WHITEBOARD) }

    // Palette Colors
    val colors = listOf(
        Color(0xFF0F172A), // Black / Dark Slate
        Color(0xFF2563EB), // APS Blue
        Color(0xFFDC2626), // Red
        Color(0xFF16A34A), // Green
        Color(0xFFD97706), // Amber
        Color(0xFF7C3AED)  // Purple
    )

    var selectedColor by remember { mutableStateOf(colors[0]) }
    var strokeWidth by remember { mutableFloatStateOf(6f) }
    var isEraser by remember { mutableStateOf(false) }

    // Path tracking
    val paths = remember { mutableStateListOf<DrawPathItem>() }
    val undonePaths = remember { mutableStateListOf<DrawPathItem>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    // Canvas background
    val canvasBgColor = if (currentMode == BoardMode.SIGNATURE) Color(0xFFFAFAFA) else Color.White

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentMode == BoardMode.WHITEBOARD) "Whiteboard Pad" else "Quick Signature Pad",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = if (currentMode == BoardMode.WHITEBOARD) "Draw & sketch ideas" else "Sign documents with clean exports",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("whiteboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Clear canvas
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            paths.clear()
                            undonePaths.clear()
                            currentPath = null
                        },
                        modifier = Modifier.testTag("whiteboard_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Canvas",
                            tint = Color(0xFFDC2626)
                        )
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Mode Selector Bar (Whiteboard vs Signature Pad)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = currentMode == BoardMode.WHITEBOARD,
                    onClick = {
                        HapticUtils.performClick(context)
                        currentMode = BoardMode.WHITEBOARD
                        strokeWidth = 6f
                        selectedColor = colors[0]
                        isEraser = false
                    },
                    label = { Text("Whiteboard Mode") },
                    leadingIcon = {
                        Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ApsBlue,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = currentMode == BoardMode.SIGNATURE,
                    onClick = {
                        HapticUtils.performClick(context)
                        currentMode = BoardMode.SIGNATURE
                        strokeWidth = 4f
                        selectedColor = Color(0xFF0F172A) // Rich black ink for signatures
                        isEraser = false
                    },
                    label = { Text("Signature Pad") },
                    leadingIcon = {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ApsBlue,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // Top Action Toolbar (Undo, Redo, Eraser, Stroke Width Slider)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Undo / Redo
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                enabled = paths.isNotEmpty(),
                                onClick = {
                                    HapticUtils.performClick(context)
                                    if (paths.isNotEmpty()) {
                                        val last = paths.removeAt(paths.lastIndex)
                                        undonePaths.add(last)
                                    }
                                },
                                modifier = Modifier.testTag("whiteboard_undo_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Undo,
                                    contentDescription = "Undo",
                                    tint = if (paths.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                )
                            }

                            IconButton(
                                enabled = undonePaths.isNotEmpty(),
                                onClick = {
                                    HapticUtils.performClick(context)
                                    if (undonePaths.isNotEmpty()) {
                                        val restored = undonePaths.removeAt(undonePaths.lastIndex)
                                        paths.add(restored)
                                    }
                                },
                                modifier = Modifier.testTag("whiteboard_redo_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Redo,
                                    contentDescription = "Redo",
                                    tint = if (undonePaths.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        // Pen vs Eraser toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isEraser) ApsBlue.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        HapticUtils.performClick(context)
                                        isEraser = false
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Brush,
                                    contentDescription = "Pen Tool",
                                    tint = if (!isEraser) ApsBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isEraser) Color(0xFFEF4444).copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        HapticUtils.performClick(context)
                                        isEraser = true
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = "Eraser Tool",
                                    tint = if (isEraser) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Color picker circles
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            colors.take(if (currentMode == BoardMode.SIGNATURE) 2 else colors.size).forEach { color ->
                                val isSelected = !isEraser && selectedColor == color
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) ApsBlue else Color(0xFFCBD5E1),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            HapticUtils.performClick(context)
                                            selectedColor = color
                                            isEraser = false
                                        }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stroke size slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Size: ${strokeWidth.toInt()}px",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(65.dp)
                        )
                        Slider(
                            value = strokeWidth,
                            onValueChange = { strokeWidth = it },
                            valueRange = 2f..32f,
                            colors = SliderDefaults.colors(
                                thumbColor = ApsBlue,
                                activeTrackColor = ApsBlue
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Interactive Drawing Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(canvasBgColor)
                    .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            ) {
                // If signature mode, draw clean guideline for signing
                if (currentMode == BoardMode.SIGNATURE && paths.isEmpty() && currentPath == null) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sign here with your finger",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(Color(0xFFCBD5E1))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sign on the line",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("drawing_canvas")
                        .pointerInput(isEraser, selectedColor, strokeWidth) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val path = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                    currentPath = path
                                    undonePaths.clear()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    currentPath?.let { p ->
                                        val activeColor = if (isEraser) canvasBgColor else selectedColor
                                        val activeWidth = if (isEraser) strokeWidth * 2.5f else strokeWidth
                                        paths.add(DrawPathItem(p, activeColor, activeWidth))
                                    }
                                    currentPath = null
                                },
                                onDragCancel = {
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    // Draw committed paths
                    paths.forEach { item ->
                        drawPath(
                            path = item.path,
                            color = item.color,
                            style = Stroke(
                                width = item.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    // Draw in-progress path
                    currentPath?.let { path ->
                        val activeColor = if (isEraser) canvasBgColor else selectedColor
                        val activeWidth = if (isEraser) strokeWidth * 2.5f else strokeWidth
                        drawPath(
                            path = path,
                            color = activeColor,
                            style = Stroke(
                                width = activeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Bottom Export Actions (Save to Gallery & Share)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save to Gallery
                    Button(
                        onClick = {
                            HapticUtils.performClick(context)
                            if (paths.isEmpty()) {
                                Toast.makeText(context, "Canvas is empty! Draw something first.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val bitmap = exportCanvasToBitmap(paths, 1080, 1080, canvasBgColor)
                            val fileName = if (currentMode == BoardMode.SIGNATURE) "Signature_${System.currentTimeMillis()}.png" else "Whiteboard_${System.currentTimeMillis()}.png"
                            ImageUtils.saveBitmapToGallery(
                                context = context,
                                bitmap = bitmap,
                                displayName = fileName,
                                format = Bitmap.CompressFormat.PNG,
                                showToast = true
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ApsBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whiteboard_save_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save PNG", fontWeight = FontWeight.Bold)
                    }

                    // Share Image
                    OutlinedButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            if (paths.isEmpty()) {
                                Toast.makeText(context, "Canvas is empty! Draw something first.", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }
                            val bitmap = exportCanvasToBitmap(paths, 1080, 1080, canvasBgColor)
                            try {
                                val cacheFile = File(context.cacheDir, "shared_draw_${System.currentTimeMillis()}.png")
                                FileOutputStream(cacheFile).use { out ->
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                                }
                                ImageUtils.shareFile(
                                    context = context,
                                    file = cacheFile,
                                    mimeType = "image/png",
                                    chooserTitle = if (currentMode == BoardMode.SIGNATURE) "Share Signature" else "Share Sketch"
                                )
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to share: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whiteboard_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Render Compose paths directly onto a standard Android Bitmap for crisp PNG export.
 */
private fun exportCanvasToBitmap(
    paths: List<DrawPathItem>,
    width: Int = 1080,
    height: Int = 1080,
    backgroundColor: Color = Color.White
): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    canvas.drawColor(backgroundColor.toArgb())

    val paint = AndroidPaint().apply {
        isAntiAlias = true
        style = AndroidPaint.Style.STROKE
        strokeCap = AndroidPaint.Cap.ROUND
        strokeJoin = AndroidPaint.Join.ROUND
    }

    paths.forEach { item ->
        paint.color = item.color.toArgb()
        paint.strokeWidth = item.strokeWidth * 1.5f // scale appropriately for export bitmap resolution
        val androidPath = item.path.asAndroidPath()
        canvas.drawPath(androidPath, paint)
    }

    return bitmap
}
