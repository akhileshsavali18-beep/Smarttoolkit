package com.aistudio.smarttoolkit.tyvz

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aistudio.smarttoolkit.tyvz.billing.BillingManager
import com.aistudio.smarttoolkit.tyvz.model.Calculators
import com.aistudio.smarttoolkit.tyvz.model.GstMode
import com.aistudio.smarttoolkit.tyvz.utils.QrUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("APS TOOLS", appName)
  }

  @Test
  fun `verify split bill calculation`() {
    val result = Calculators.calculateSplitBill(billAmount = 1000.0, tipPercent = 10.0, numPeople = 2)
    assertEquals(100.0, result.tipAmount, 0.01)
    assertEquals(1100.0, result.totalWithTip, 0.01)
    assertEquals(550.0, result.perPersonTotal, 0.01)
  }

  @Test
  fun `verify text statistics calculation`() {
    val text = "APS TOOLS is fast and reliable. Simple tools, smart results!"
    val stats = Calculators.calculateTextStats(text)
    assertEquals(10, stats.wordCount)
    assertEquals(2, stats.sentenceCount)
    assertTrue(stats.charCount > 0)
    assertTrue(stats.charCountNoSpaces > 0)
  }

  @Test
  fun `verify gst calculation`() {
    val result = Calculators.calculateGst(amount = 1000.0, ratePercent = 18.0, mode = GstMode.ADD_GST)
    assertEquals(180.0, result.totalGst, 0.01)
    assertEquals(1180.0, result.totalAmount, 0.01)
    assertEquals(90.0, result.cgst, 0.01)
    assertEquals(90.0, result.sgst, 0.01)
  }

  @Test
  fun `verify billing manager test toggle`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val billing = BillingManager(context)
    
    assertTrue(billing.isPremium.value)
    
  }

  @Test
  fun `verify qr code generator`() {
    val bitmap = QrUtils.generateQrBitmap("https://example.com", size = 200)
    assertNotNull(bitmap)
    assertEquals(200, bitmap?.width)
    assertEquals(200, bitmap?.height)
  }

  @Test
  fun `verify recent tools starts empty and only records when explicitly opened`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Clear any existing preferences to ensure clean state
    context.getSharedPreferences("aps_tools_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    val prefs = com.aistudio.smarttoolkit.tyvz.model.AppPreferencesManager(context)

    assertTrue("Recent tools must start completely empty", prefs.recentTools.value.isEmpty())

    // Explicitly open/record a tool
    prefs.recordRecentTool(com.aistudio.smarttoolkit.tyvz.model.AppScreen.BMI_CALCULATOR.name)
    assertEquals(1, prefs.recentTools.value.size)
    assertEquals(com.aistudio.smarttoolkit.tyvz.model.AppScreen.BMI_CALCULATOR.name, prefs.recentTools.value.first())
  }

  @Test
  fun `verify favourites starts empty and only adds when star clicked`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences("aps_tools_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    val prefs = com.aistudio.smarttoolkit.tyvz.model.AppPreferencesManager(context)

    assertTrue("Favourites must start with emptySet", prefs.favoriteTools.value.isEmpty())

    // Toggle favorite (star clicked)
    prefs.toggleFavorite(com.aistudio.smarttoolkit.tyvz.model.AppScreen.PHOTO_COMPRESSOR.name)
    assertTrue(prefs.favoriteTools.value.contains(com.aistudio.smarttoolkit.tyvz.model.AppScreen.PHOTO_COMPRESSOR.name))
    assertEquals(1, prefs.favoriteTools.value.size)

    // Toggle again removes it
    prefs.toggleFavorite(com.aistudio.smarttoolkit.tyvz.model.AppScreen.PHOTO_COMPRESSOR.name)
    assertTrue(prefs.favoriteTools.value.isEmpty())
  }

  @Test
  fun `verify tool categories enum titles and distinct mappings`() {
    assertEquals("Media & Documents", com.aistudio.smarttoolkit.tyvz.model.ToolCategory.MEDIA_DOC.title)
    assertEquals("Finance & Math Utilities", com.aistudio.smarttoolkit.tyvz.model.ToolCategory.FINANCE.title)
    assertEquals("Daily Utilities & Device Sensors", com.aistudio.smarttoolkit.tyvz.model.ToolCategory.DAILY_HEALTH.title)
    assertEquals("Health & Fitness", com.aistudio.smarttoolkit.tyvz.model.ToolCategory.HEALTH_FITNESS.title)
    assertEquals("Science & Converters", com.aistudio.smarttoolkit.tyvz.model.ToolCategory.SCIENCE_CONVERTERS.title)
  }

  @Test
  fun `verify total app screens and tools cross 50 milestone`() {
    val totalScreens = com.aistudio.smarttoolkit.tyvz.model.AppScreen.values().size
    assertTrue("Total AppScreen definitions should be at least 50 (actual: $totalScreens)", totalScreens >= 50)
  }
}
