package com.aistudio.smarttoolkit.tyvz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.smarttoolkit.tyvz.model.Calculators
import com.aistudio.smarttoolkit.tyvz.ui.components.AppInputField
import com.aistudio.smarttoolkit.tyvz.ui.components.copyToClipboard
import com.aistudio.smarttoolkit.tyvz.ui.theme.UnitCyan
import com.aistudio.smarttoolkit.tyvz.utils.HapticUtils
import java.text.DecimalFormat

enum class UnitCategory(val title: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature")
}

data class UnitItem(val id: String, val name: String, val symbol: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }

    val lengthUnits = remember {
        listOf(
            UnitItem("m", "Meter", "m"),
            UnitItem("km", "Kilometer", "km"),
            UnitItem("cm", "Centimeter", "cm"),
            UnitItem("mm", "Millimeter", "mm"),
            UnitItem("ft", "Foot", "ft"),
            UnitItem("in", "Inch", "in"),
            UnitItem("yd", "Yard", "yd"),
            UnitItem("mi", "Mile", "mi")
        )
    }

    val weightUnits = remember {
        listOf(
            UnitItem("kg", "Kilogram", "kg"),
            UnitItem("g", "Gram", "g"),
            UnitItem("mg", "Milligram", "mg"),
            UnitItem("lb", "Pound", "lb"),
            UnitItem("oz", "Ounce", "oz"),
            UnitItem("t", "Metric Ton", "t")
        )
    }

    val tempUnits = remember {
        listOf(
            UnitItem("c", "Celsius", "°C"),
            UnitItem("f", "Fahrenheit", "°F"),
            UnitItem("k", "Kelvin", "K")
        )
    }

    val currentUnitList = when (selectedCategory) {
        UnitCategory.LENGTH -> lengthUnits
        UnitCategory.WEIGHT -> weightUnits
        UnitCategory.TEMPERATURE -> tempUnits
    }

    var fromUnitId by remember(selectedCategory) {
        mutableStateOf(
            when (selectedCategory) {
                UnitCategory.LENGTH -> "m"
                UnitCategory.WEIGHT -> "kg"
                UnitCategory.TEMPERATURE -> "c"
            }
        )
    }

    var toUnitId by remember(selectedCategory) {
        mutableStateOf(
            when (selectedCategory) {
                UnitCategory.LENGTH -> "ft"
                UnitCategory.WEIGHT -> "lb"
                UnitCategory.TEMPERATURE -> "f"
            }
        )
    }

    var inputValue by remember(selectedCategory) {
        mutableStateOf(
            when (selectedCategory) {
                UnitCategory.LENGTH -> "1"
                UnitCategory.WEIGHT -> "1"
                UnitCategory.TEMPERATURE -> "25"
            }
        )
    }

    val parsedInput = inputValue.toDoubleOrNull() ?: 0.0

    val convertedValue = remember(parsedInput, fromUnitId, toUnitId, selectedCategory) {
        when (selectedCategory) {
            UnitCategory.LENGTH -> Calculators.convertLength(parsedInput, fromUnitId, toUnitId)
            UnitCategory.WEIGHT -> Calculators.convertWeight(parsedInput, fromUnitId, toUnitId)
            UnitCategory.TEMPERATURE -> Calculators.convertTemperature(parsedInput, fromUnitId, toUnitId)
        }
    }

    val formatter = remember { DecimalFormat("#,##0.######") }
    val formattedResult = formatter.format(convertedValue)

    val fromUnit = currentUnitList.find { it.id == fromUnitId } ?: currentUnitList.first()
    val toUnit = currentUnitList.find { it.id == toUnitId } ?: currentUnitList.last()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Unit Converter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("unit_converter_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticUtils.performClick(context)
                            inputValue = "1"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = UnitCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Tabs: Length, Weight, Temperature
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                UnitCategory.values().forEach { cat ->
                    val isSelected = cat == selectedCategory
                    val catIcon = when (cat) {
                        UnitCategory.LENGTH -> Icons.Default.Straighten
                        UnitCategory.WEIGHT -> Icons.Default.FitnessCenter
                        UnitCategory.TEMPERATURE -> Icons.Default.Thermostat
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) UnitCyan else Color.Transparent)
                            .clickable {
                                if (!isSelected) {
                                    HapticUtils.performClick(context)
                                    selectedCategory = cat
                                }
                            }
                            .padding(vertical = 8.dp)
                            .testTag("unit_category_${cat.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = catIcon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cat.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Input and From/To Selection Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AppInputField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = "Enter Value",
                        placeholder = "e.g. 100",
                        testTag = "unit_input_value"
                    )

                    // From Unit Selector
                    UnitDropdownSelector(
                        label = "From Unit",
                        selectedUnit = fromUnit,
                        units = currentUnitList,
                        onUnitSelected = { fromUnitId = it.id }
                    )

                    // Swap Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                HapticUtils.performClick(context)
                                val temp = fromUnitId
                                fromUnitId = toUnitId
                                toUnitId = temp
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(UnitCyan.copy(alpha = 0.12f))
                                .testTag("swap_units_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Units",
                                tint = UnitCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // To Unit Selector
                    UnitDropdownSelector(
                        label = "To Unit",
                        selectedUnit = toUnit,
                        units = currentUnitList,
                        onUnitSelected = { toUnitId = it.id }
                    )
                }
            }

            // Converted Output Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Converted Result",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$inputValue ${fromUnit.symbol} =",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$formattedResult ${toUnit.symbol}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = UnitCyan
                            )
                        }

                        IconButton(
                            onClick = {
                                copyToClipboard(
                                    context,
                                    "Result",
                                    "$formattedResult ${toUnit.symbol}"
                                )
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("copy_result_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Result",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Quick Reference / Multi-Unit Breakdown Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Equivalent Values in All ${selectedCategory.title} Units",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider()

                    currentUnitList.forEach { unit ->
                        val eqValue = when (selectedCategory) {
                            UnitCategory.LENGTH -> Calculators.convertLength(parsedInput, fromUnitId, unit.id)
                            UnitCategory.WEIGHT -> Calculators.convertWeight(parsedInput, fromUnitId, unit.id)
                            UnitCategory.TEMPERATURE -> Calculators.convertTemperature(parsedInput, fromUnitId, unit.id)
                        }
                        val isCurrentTarget = unit.id == toUnitId

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrentTarget) UnitCyan.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable {
                                    HapticUtils.performClick(context)
                                    toUnitId = unit.id
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${unit.name} (${unit.symbol})",
                                fontSize = 13.sp,
                                fontWeight = if (isCurrentTarget) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrentTarget) UnitCyan else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = formatter.format(eqValue),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCurrentTarget) UnitCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdownSelector(
    label: String,
    selectedUnit: UnitItem,
    units: List<UnitItem>,
    onUnitSelected: (UnitItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "${selectedUnit.name} (${selectedUnit.symbol})",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text("${unit.name} (${unit.symbol})") },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
