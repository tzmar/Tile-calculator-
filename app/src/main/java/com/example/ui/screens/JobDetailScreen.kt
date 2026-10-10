package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppScreen
import com.example.TileCalculatorViewModel
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobDetailScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val job = uiState.activeJob ?: return
    val calc = uiState.calculatedResult ?: TileCalculatorEngine.calculateJob(job)
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = { viewModel.navigateBack() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("job_back_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextForestDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = job.jobName.ifBlank { "Job Details" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = TextForestDeep,
                        maxLines = 1
                    )
                    Text(
                        text = "${job.areas.size} areas · ${String.format(Locale.getDefault(), "%.1f m²", calc.grandTotalAreaSqM)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Save button
                Surface(
                    onClick = {
                        viewModel.saveJobExplicitly()
                        Toast.makeText(context, "Job saved to phone", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("save_job_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            tint = LiquidGrassPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextForestDeep
                            )
                        )
                    }
                }

                // Quotation button
                Surface(
                    onClick = { viewModel.navigateTo(AppScreen.QUOTATION_VIEW) },
                    shape = RoundedCornerShape(16.dp),
                    color = LiquidGrassPrimary,
                    shadowElevation = 3.dp,
                    modifier = Modifier.testTag("quotation_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Quotation",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Scrollable Job Contents
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 36.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Job Information & Unit Selector Card
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    LiquidSectionHeader(
                        title = "Job Information",
                        helper = "Name the site & pick the measuring tape unit"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LiquidTextField(
                        value = job.jobName,
                        onValueChange = { viewModel.updateActiveJob { j -> j.copy(jobName = it) } },
                        label = "Job name",
                        placeholder = "e.g. Mr Kgosi - 3 bedroom house",
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        testTag = "job_name_input"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            LiquidTextField(
                                value = job.clientName,
                                onValueChange = { viewModel.updateActiveJob { j -> j.copy(clientName = it) } },
                                label = "Client (optional)",
                                placeholder = "Mr Kgosi",
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LiquidTextField(
                                value = job.siteAddress,
                                onValueChange = { viewModel.updateActiveJob { j -> j.copy(siteAddress = it) } },
                                label = "Site / Plot (optional)",
                                placeholder = "Gaborone Plot 1234",
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Unit selector pills: Meters / Centimeters / Feet
                    Text(
                        text = "Measurement unit (applies to all rooms):",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextForestDeep
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MeasurementUnit.values().forEach { u ->
                            LiquidPillChip(
                                label = "${u.label} (${u.symbol})",
                                isSelected = job.unit == u,
                                onClick = { viewModel.setMeasurementUnit(u) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Areas List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AREAS TO TILE (${job.areas.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        ),
                        color = TextForestMuted
                    )
                }
            }

            // Area Items
            itemsIndexed(job.areas) { index, area ->
                val areaCalc = calc.areaResults.getOrNull(index)
                AreaCardItem(
                    index = index,
                    area = area,
                    areaCalc = areaCalc,
                    unit = job.unit,
                    onEdit = { viewModel.openAreaEditor(index) },
                    onDuplicate = { viewModel.duplicateArea(index) },
                    onDelete = { viewModel.requestDeleteArea(index) },
                    onReset = { viewModel.resetArea(index) }
                )
            }

            // Big "Add Area" Selector Card
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    LiquidSectionHeader(
                        title = "Add Area",
                        helper = "Tap a room type to measure and add to this job"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiquidPillChip(
                            label = "Room floor",
                            isSelected = false,
                            onClick = { viewModel.addNewArea(AreaType.ROOM_FLOOR) },
                            modifier = Modifier.weight(1f),
                            testTag = "add_room_floor_button"
                        )
                        LiquidPillChip(
                            label = "Bathroom",
                            isSelected = false,
                            onClick = { viewModel.addNewArea(AreaType.BATHROOM) },
                            modifier = Modifier.weight(1f),
                            testTag = "add_bathroom_button"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiquidPillChip(
                            label = "Veranda",
                            isSelected = false,
                            onClick = { viewModel.addNewArea(AreaType.VERANDA) },
                            modifier = Modifier.weight(1f),
                            testTag = "add_veranda_button"
                        )
                        LiquidPillChip(
                            label = "Wall only",
                            isSelected = false,
                            onClick = { viewModel.addNewArea(AreaType.WALL_ONLY) },
                            modifier = Modifier.weight(1f),
                            testTag = "add_wall_only_button"
                        )
                    }
                }
            }

            // TILE CHOICE ("At the shop")
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LiquidSectionHeader(
                            title = "Tile Choice (\"At the shop\")",
                            helper = if (job.isTileChosen) "Client chose tile · Quantities calculated" else "Measurements recorded. Pick tile when at shop."
                        )

                        // Switch: Is tile chosen?
                        Switch(
                            checked = job.isTileChosen,
                            onCheckedChange = { viewModel.toggleTileChosen(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LiquidGrassPrimary
                            ),
                            modifier = Modifier.testTag("tile_chosen_switch")
                        )
                    }

                    if (!job.isTileChosen) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFFFEF3C7).copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Storefront,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Waiting for tile choice at the shop",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    )
                                    Text(
                                        text = "Turn the switch ON when the client chooses their tile size and box count.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }
                    } else {
                        // Tile Chooser controls
                        Spacer(modifier = Modifier.height(14.dp))
                        TileChooserControls(
                            job = job,
                            onUpdateFloorTile = { viewModel.updateSharedFloorTile(it) },
                            onUpdateWallTile = { viewModel.updateSharedWallTile(it) },
                            onToggleSameTileAll = { viewModel.updateActiveJob { j -> j.copy(sameTileForAllAreas = it) } },
                            onToggleSameTileWalls = { viewModel.updateActiveJob { j -> j.copy(sameTileForWallsAndSides = it) } }
                        )
                    }
                }
            }

            // TILE ADHESIVE (CEMENT) AND BED THICKNESS
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    LiquidSectionHeader(
                        title = "Tile Adhesive & Bed Thickness",
                        helper = "Cement floors in Africa are rarely level. Average adhesive thickness under tile."
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AdhesiveSectionControls(
                        adhesiveConfig = job.adhesiveConfig,
                        onUpdate = { viewModel.updateAdhesiveConfig(it) }
                    )
                }
            }

            // RESULTS SECTION
            item {
                Text(
                    text = "CALCULATED QUANTITIES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    ),
                    color = TextForestMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Hero Card: BOXES TO BUY (most prominent)
            item {
                HeroBoxesCard(calc = calc)
            }

            // Materials to Buy Grouped Cards
            item {
                MaterialsSummaryCard(calc = calc)
            }

            // Bottom Footnote
            item {
                Surface(
                    color = Color.White.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Disclaimer",
                            tint = TextForestMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Estimates only. Always check the coverage printed on your bag.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = TextForestMuted
                            )
                        )
                    }
                }
            }
        }
    }

    // Confirm Delete Area Dialog
    if (uiState.areaIndexToDelete != null) {
        val idx = uiState.areaIndexToDelete!!
        val areaName = job.areas.getOrNull(idx)?.name ?: "Area"
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteAreaDialog() },
            title = { Text("Delete Area?") },
            text = { Text("Are you sure you want to remove '$areaName' from this job?") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteArea() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteAreaDialog() }) {
                    Text("Cancel", color = TextForestMuted)
                }
            }
        )
    }
}

@Composable
private fun AreaCardItem(
    index: Int,
    area: AreaItem,
    areaCalc: AreaCalculatedResult?,
    unit: MeasurementUnit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onReset: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = LiquidGrassPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = area.type.title.uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = area.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TextForestDeep
                    )
                }

                // Action buttons: Edit, Duplicate, Delete
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = LiquidGrassDeep, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = "Duplicate", tint = TextForestMuted, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Size & m² details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val sizeText = when (area.type) {
                    AreaType.ROOM_FLOOR -> "${area.lengthInput} x ${area.widthInput} ${unit.symbol}"
                    AreaType.BATHROOM -> "${area.lengthInput} x ${area.widthInput} ${unit.symbol}"
                    AreaType.VERANDA -> "${area.lengthInput} x ${area.widthInput} ${unit.symbol}"
                    AreaType.WALL_ONLY -> "${area.wallOnlyLengthInput} x ${area.wallOnlyHeightInput} ${unit.symbol}"
                }
                Text(
                    text = "Size: $sizeText",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = TextForestMuted
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.2f m²", areaCalc?.totalAreaSqM ?: 0.0),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = LiquidGrassDeep
                )

                if (areaCalc != null && areaCalc.isTileChosen) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${areaCalc.floorBoxesToBuy} boxes",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = LiquidGrassPrimary
                        )
                    )
                }
            }

            // Skirting and Edge Strips info
            if (areaCalc != null) {
                if (areaCalc.skirtingEnabled && areaCalc.skirtingLengthMeters > 0) {
                    Text(
                        text = "Skirting: ${String.format(Locale.getDefault(), "%.1f m", areaCalc.skirtingLengthMeters)} (${areaCalc.skirtingHeightCm.toInt()} cm, ${areaCalc.skirtingMethod.label})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
                if (areaCalc.edgeStripEnabled && areaCalc.edgeStripLengthMeters > 0) {
                    Text(
                        text = "Edge strips: ${String.format(Locale.getDefault(), "%.1f m", areaCalc.edgeStripLengthMeters)} (${areaCalc.edgeStripMaterial.label})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun TileChooserControls(
    job: Job,
    onUpdateFloorTile: ((TileSpec) -> TileSpec) -> Unit,
    onUpdateWallTile: ((TileSpec) -> TileSpec) -> Unit,
    onToggleSameTileAll: (Boolean) -> Unit,
    onToggleSameTileWalls: (Boolean) -> Unit
) {
    val floorSpec = job.sharedFloorTileSpec

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Tile size chips
        Text(
            text = "Floor tile size:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sizes = listOf(
                Pair("30 x 30 cm", Pair(30.0, 30.0)),
                Pair("40 x 40 cm", Pair(40.0, 40.0)),
                Pair("60 x 60 cm", Pair(60.0, 60.0)),
                Pair("60 x 120 cm", Pair(60.0, 120.0)),
                Pair("80 x 80 cm", Pair(80.0, 80.0)),
                Pair("Custom size", Pair(0.0, 0.0))
            )

            sizes.forEach { (label, dims) ->
                val isSelected = floorSpec.sizeLabel == label
                LiquidPillChip(
                    label = label,
                    isSelected = isSelected,
                    onClick = {
                        if (label == "Custom size") {
                            onUpdateFloorTile { it.copy(sizeLabel = label, isCustom = true) }
                        } else {
                            onUpdateFloorTile { it.copy(sizeLabel = label, lengthCm = dims.first, widthCm = dims.second, isCustom = false) }
                        }
                    }
                )
            }
        }

        // Hint: 600 x 600 mm = 60 x 60 cm
        Surface(
            color = LiquidGrassPale.copy(alpha = 0.6f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Hint: 600 x 600 mm = 60 x 60 cm",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = LiquidGrassDeep),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        // Custom size fields if chosen
        if (floorSpec.isCustom) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LiquidTextField(
                        value = floorSpec.lengthCm.toString(),
                        onValueChange = {
                            val v = TileCalculatorEngine.parseNum(it)
                            onUpdateFloorTile { s -> s.copy(lengthCm = v) }
                        },
                        label = "Length (cm)",
                        placeholder = "50",
                        keyboardType = KeyboardType.Decimal
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    LiquidTextField(
                        value = floorSpec.widthCm.toString(),
                        onValueChange = {
                            val v = TileCalculatorEngine.parseNum(it)
                            onUpdateFloorTile { s -> s.copy(widthCm = v) }
                        },
                        label = "Width (cm)",
                        placeholder = "50",
                        keyboardType = KeyboardType.Decimal
                    )
                }
            }
        }

        // Tiles per box input
        LiquidTextField(
            value = floorSpec.tilesPerBox.toString(),
            onValueChange = {
                val c = it.trim().toIntOrNull() ?: 12
                onUpdateFloorTile { s -> s.copy(tilesPerBox = c) }
            },
            label = "Number of tiles in one box",
            placeholder = "12",
            suffix = "tiles / box",
            keyboardType = KeyboardType.Number,
            testTag = "tiles_per_box_input"
        )

        // Waste Allowance chips
        Text(
            text = "Waste allowance (for cuts & breakages):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5.0, 10.0, 15.0, 20.0).forEach { w ->
                LiquidPillChip(
                    label = "${w.toInt()}%",
                    isSelected = floorSpec.wastePercent == w,
                    onClick = { onUpdateFloorTile { it.copy(wastePercent = w) } },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Spacer size chips
        Text(
            text = "Tile spacers size:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1.5, 2.0, 3.0, 4.0, 5.0, 6.0, 8.0, 10.0).forEach { sp ->
                LiquidPillChip(
                    label = "${if (sp == 1.5) "1.5" else sp.toInt().toString()} mm",
                    isSelected = floorSpec.spacerMm == sp,
                    onClick = { onUpdateFloorTile { it.copy(spacerMm = sp) } }
                )
            }
        }
        Text(
            text = "Check the size on the pack of spacers.",
            style = MaterialTheme.typography.bodySmall,
            color = TextForestMuted
        )

        // Switches:
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Same tile for all areas",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextForestDeep
            )
            Switch(
                checked = job.sameTileForAllAreas,
                onCheckedChange = onToggleSameTileAll,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Use the same tile on walls & sides",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextForestDeep
            )
            Switch(
                checked = job.sameTileForWallsAndSides,
                onCheckedChange = onToggleSameTileWalls,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
            )
        }
    }
}

@Composable
private fun AdhesiveSectionControls(
    adhesiveConfig: AdhesiveConfig,
    onUpdate: ((AdhesiveConfig) -> AdhesiveConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Brand selector chips
        Text(
            text = "Adhesive brand:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdhesiveBrand.values().forEach { b ->
                LiquidPillChip(
                    label = b.displayName.substringBefore("(").trim(),
                    isSelected = adhesiveConfig.brand == b,
                    onClick = { onUpdate { it.copy(brand = b) } }
                )
            }
        }

        // Bed thickness buttons
        Text(
            text = "Floor adhesive thickness under tile:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val thicknesses = listOf(
                Pair(5.0, "Level 5 mm"),
                Pair(8.0, "Uneven 8 mm"),
                Pair(12.0, "Very uneven 12 mm")
            )
            thicknesses.forEach { (t, lbl) ->
                LiquidPillChip(
                    label = lbl,
                    isSelected = adhesiveConfig.floorThicknessMm == t,
                    onClick = { onUpdate { it.copy(floorThicknessMm = t) } },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Custom thickness entry
        LiquidTextField(
            value = adhesiveConfig.floorThicknessMm.toString(),
            onValueChange = {
                val v = TileCalculatorEngine.parseNum(it)
                onUpdate { s -> s.copy(floorThicknessMm = v) }
            },
            label = "Average floor bed thickness (mm):",
            placeholder = "5",
            suffix = "mm"
        )

        // Tip & warnings
        Text(
            text = "Tip: Lay a straight edge on the floor and measure the deepest low spot.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextForestMuted)
        )

        if (adhesiveConfig.floorThicknessMm > 10.0) {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "A bed thicker than 10 mm uses a lot of adhesive. Levelling the floor first with a sand-cement screed is usually cheaper.",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = Color(0xFF92400E)),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Bonding liquid switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Include bonding liquid (primer)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextForestDeep
                )
                Text(
                    text = "Replaces mixing water (5 litres per adhesive bag)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextForestMuted
                )
            }
            Switch(
                checked = adhesiveConfig.includeBondingLiquid,
                onCheckedChange = { onUpdate { s -> s.copy(includeBondingLiquid = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
            )
        }
    }
}

@Composable
private fun HeroBoxesCard(calc: JobCalculatedResult) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = LiquidGrassDeep.copy(alpha = 0.45f))
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF059669),
                        Color(0xFF047857),
                        Color(0xFF064E3B)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .testTag("boxes_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.20f),
                shape = CircleShape
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BOXES TO BUY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.4.sp,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (calc.isTileChosen) {
                Text(
                    text = "${calc.totalFloorBoxes}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 72.sp,
                        lineHeight = 76.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = if (calc.totalFloorBoxes == 1) "BOX OF TILES" else "BOXES OF TILES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFFD1FAE5)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "You will use ${calc.totalFloorTilesNeeded} of the ${calc.totalFloorTilesInBoxes} tiles, so you will have ${calc.totalFloorSpareTiles} spare.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            } else {
                Text(
                    text = "Awaiting tile choice",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Text(
                    text = "Total measured area: ${String.format(Locale.getDefault(), "%.2f m²", calc.grandTotalAreaSqM)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD1FAE5)
                )
            }
        }
    }
}

@Composable
private fun MaterialsSummaryCard(calc: JobCalculatedResult) {
    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
        LiquidSectionHeader(
            title = "Materials To Buy",
            helper = "Complete shopping list for hardware store"
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Adhesive
        MaterialSummaryRow(
            icon = Icons.Outlined.Handyman,
            title = "Tile Adhesive (Cement)",
            subtitle = "${calc.adhesiveBrandName} (${calc.floorThicknessMm.toInt()} mm bed)",
            quantity = "${calc.adhesiveBags} bags (20 kg)"
        )

        Divider(color = LiquidGrassPale, thickness = 1.dp)

        // Grout
        MaterialSummaryRow(
            icon = Icons.Outlined.CheckCircle,
            title = "Tile Grout",
            subtitle = "Standard 5 kg bags (covers 10 m² each)",
            quantity = "${calc.groutBags} bags (5 kg)"
        )

        if (calc.bondingLiquidLitres > 0) {
            Divider(color = LiquidGrassPale, thickness = 1.dp)
            MaterialSummaryRow(
                icon = Icons.Outlined.WaterDrop,
                title = "Bonding Liquid (Optional)",
                subtitle = "Replaces mixing water for extra strength",
                quantity = "${calc.bondingLiquidLitres} litres"
            )
        }

        if (calc.isTileChosen && calc.totalSpacersCount > 0) {
            Divider(color = LiquidGrassPale, thickness = 1.dp)
            MaterialSummaryRow(
                icon = Icons.Outlined.GridOn,
                title = "Tile Spacers",
                subtitle = "Check size on pack (${calc.spacerSizeMm.toInt()} mm)",
                quantity = "${calc.totalSpacersCount} pieces"
            )
        }

        if (calc.plasticEdgeStripPieces > 0) {
            Divider(color = LiquidGrassPale, thickness = 1.dp)
            MaterialSummaryRow(
                icon = Icons.Outlined.HorizontalRule,
                title = "Plastic Edge Strips",
                subtitle = "${calc.plasticTileThicknessMm} mm thickness, 2.5 m length each",
                quantity = "${calc.plasticEdgeStripPieces} pieces"
            )
        }

        if (calc.metalEdgeStripPieces > 0) {
            Divider(color = LiquidGrassPale, thickness = 1.dp)
            MaterialSummaryRow(
                icon = Icons.Outlined.HorizontalRule,
                title = "Metal Edge Strips",
                subtitle = "${calc.metalTileThicknessMm} mm thickness, 2.5 m length each",
                quantity = "${calc.metalEdgeStripPieces} pieces"
            )
        }
    }
}

@Composable
private fun MaterialSummaryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    quantity: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LiquidGrassPale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LiquidGrassPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextForestDeep
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextForestMuted
            )
        }
        Surface(
            color = LiquidGrassPrimary,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = quantity,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}
