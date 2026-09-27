package com.aistudio.smarttoolkit.tyvz.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.aistudio.smarttoolkit.tyvz.ui.components.ResultValueRow
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.DayBlue
import com.aistudio.smarttoolkit.tyvz.ui.theme.DayBlueBg
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import com.aistudio.smarttoolkit.tyvz.utils.ImageUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayFinderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = remember { Calendar.getInstance() }

    var selectedYear by remember { mutableIntStateOf(today.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(today.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val cal = remember(selectedYear, selectedMonth, selectedDay) {
        Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val dayOfWeekName = remember(cal) {
        SimpleDateFormat("EEEE", Locale.getDefault()).format(cal.time)
    }

    val formattedDate = remember(cal) {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(cal.time)
    }

    val isLeapYear = remember(selectedYear) {
        (selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0)
    }

    val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    val totalDaysInYear = if (isLeapYear) 366 else 365
    val weekOfYear = cal.get(Calendar.WEEK_OF_YEAR)

    val diffDays = remember(cal) {
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMs = cal.timeInMillis - todayCal.timeInMillis
        TimeUnit.MILLISECONDS.toDays(diffMs)
    }

    val relativeText = when {
        diffDays == 0L -> "Today"
        diffDays == 1L -> "Tomorrow"
        diffDays == -1L -> "Yesterday"
        diffDays > 0 -> "In $diffDays days"
        else -> "${Math.abs(diffDays)} days ago"
    }

    val zodiacSign = remember(selectedMonth, selectedDay) {
        when (selectedMonth) {
            Calendar.JANUARY -> if (selectedDay < 20) "Capricorn ♑" else "Aquarius ♒"
            Calendar.FEBRUARY -> if (selectedDay < 19) "Aquarius ♒" else "Pisces ♓"
            Calendar.MARCH -> if (selectedDay < 21) "Pisces ♓" else "Aries ♈"
            Calendar.APRIL -> if (selectedDay < 20) "Aries ♈" else "Taurus ♉"
            Calendar.MAY -> if (selectedDay < 21) "Taurus ♉" else "Gemini ♊"
            Calendar.JUNE -> if (selectedDay < 21) "Gemini ♊" else "Cancer ♋"
            Calendar.JULY -> if (selectedDay < 23) "Cancer ♋" else "Leo ♌"
            Calendar.AUGUST -> if (selectedDay < 23) "Leo ♌" else "Virgo ♍"
            Calendar.SEPTEMBER -> if (selectedDay < 23) "Virgo ♍" else "Libra ♎"
            Calendar.OCTOBER -> if (selectedDay < 23) "Libra ♎" else "Scorpio ♏"
            Calendar.NOVEMBER -> if (selectedDay < 22) "Scorpio ♏" else "Sagittarius ♐"
            Calendar.DECEMBER -> if (selectedDay < 22) "Sagittarius ♐" else "Capricorn ♑"
            else -> "Aries ♈"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Day Finder",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        HapticUtils.performClick(context)
                        val now = Calendar.getInstance()
                        selectedYear = now.get(Calendar.YEAR)
                        selectedMonth = now.get(Calendar.MONTH)
                        selectedDay = now.get(Calendar.DAY_OF_MONTH)
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset to Today")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("day_finder_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Picker Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "SELECT ANY DATE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    OutlinedCard(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticUtils.performClick(context)
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        selectedYear = y
                                        selectedMonth = m
                                        selectedDay = d
                                    },
                                    selectedYear,
                                    selectedMonth,
                                    selectedDay
                                ).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(formattedDate, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text("Tap to change date", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = DayBlue)
                        }
                    }

                    // Quick buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val now = Calendar.getInstance()
                                selectedYear = now.get(Calendar.YEAR)
                                selectedMonth = now.get(Calendar.MONTH)
                                selectedDay = now.get(Calendar.DAY_OF_MONTH)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Today", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                selectedYear = 2000
                                selectedMonth = Calendar.JANUARY
                                selectedDay = 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Y2K (2000)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val now = Calendar.getInstance()
                                selectedYear = now.get(Calendar.YEAR) + 1
                                selectedMonth = Calendar.JANUARY
                                selectedDay = 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next Year", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Day Result Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DayBlueBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = DayBlue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "DAY OF THE WEEK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DayBlue,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DayBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = relativeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DayBlue
                            )
                        }
                    }

                    Text(
                        text = dayOfWeekName,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E3A8A)
                    )

                    HorizontalDivider(color = DayBlue.copy(alpha = 0.2f))

                    ResultValueRow(label = "Selected Date", value = formattedDate)
                    ResultValueRow(
                        label = "Leap Year Status",
                        value = if (isLeapYear) "Yes, Leap Year (366 days)" else "No, Standard Year (365 days)"
                    )
                    ResultValueRow(label = "Day Number of Year", value = "Day $dayOfYear of $totalDaysInYear")
                    ResultValueRow(label = "Week of the Year", value = "Week $weekOfYear")
                    ResultValueRow(label = "Zodiac Sign", value = zodiacSign)

                    HorizontalDivider(color = DayBlue.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "$formattedDate was a $dayOfWeekName\nDay $dayOfYear of $totalDaysInYear (Week $weekOfYear)\nLeap Year: ${if (isLeapYear) "Yes" else "No"}\nZodiac: $zodiacSign"
                                copyToClipboard(context, "Day Information", text)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DayBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareText = "Date Facts:\nDate: $formattedDate\nDay of Week: $dayOfWeekName ($relativeText)\nDay $dayOfYear / $totalDaysInYear (Week $weekOfYear)\nZodiac: $zodiacSign\n\nChecked via APS TOOLS"
                                ImageUtils.shareText(context, shareText, "Share Date")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = DayBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", color = DayBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
