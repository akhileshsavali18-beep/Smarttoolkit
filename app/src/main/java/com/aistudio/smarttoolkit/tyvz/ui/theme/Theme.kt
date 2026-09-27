package com.aistudio.smarttoolkit.tyvz.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
  darkColorScheme(
    primary = IndigoDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = IndigoDarkContainer,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = SkySecondary,
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = Color(0xFF0369A1),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = CategoryFinanceGreen,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ApsBlue,
    onPrimary = Color.White,
    primaryContainer = ApsBlueContainer,
    onPrimaryContainer = ApsOnBlueContainer,
    secondary = SkySecondary,
    onSecondary = Color.White,
    secondaryContainer = SkySecondaryContainer,
    onSecondaryContainer = SkyOnSecondaryContainer,
    tertiary = CategoryFinanceGreen,
    onTertiary = Color.White,
    tertiaryContainer = CategoryFinanceBg,
    onTertiaryContainer = CategoryFinanceGreen,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        val insetsController = WindowCompat.getInsetsController(window, view)
        // In Light Theme: surface/background is light, so appearanceLightStatusBars = true (dark/black icons)
        // In Dark Theme: surface/background is dark, so appearanceLightStatusBars = false (white icons)
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme

        // Seamless blend with app surface background
        val statusBarColor = colorScheme.surface.toArgb()
        window.statusBarColor = statusBarColor
        window.navigationBarColor = colorScheme.surface.toArgb()
      }
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun ApsToolsTheme(
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val preferencesManager = androidx.compose.runtime.remember { com.aistudio.smarttoolkit.tyvz.model.AppPreferencesManager.getInstance(context) }
  val themeMode by preferencesManager.themeMode.collectAsState()
  val useDynamicColor by preferencesManager.useDynamicColor.collectAsState()

  val isDarkTheme = when (themeMode) {
    com.aistudio.smarttoolkit.tyvz.model.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    com.aistudio.smarttoolkit.tyvz.model.ThemeMode.LIGHT -> false
    com.aistudio.smarttoolkit.tyvz.model.ThemeMode.DARK -> true
  }

  MyApplicationTheme(
    darkTheme = isDarkTheme,
    dynamicColor = useDynamicColor,
    content = content
  )
}

