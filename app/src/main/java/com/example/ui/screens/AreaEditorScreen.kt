package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TileCalculatorViewModel
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AreaEditorScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val area = uiState.activeArea ?: return
    val job = uiState.activeJob ?: return
    val unit = job.unit

    BackHandler {
        viewModel.saveActiveArea()
    }

    // Check if any dimension is > 100 m
    val lengthM = unit.toMeters(TileCalculatorEngine.parseNum(area.lengthInput))
    val widthM = unit.toMeters(TileCalculatorEngine.parseNum(area.widthInput))
    val isVeryLarge = lengthM > 100.0 || widthM > 100.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = { viewModel.saveActiveArea() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(38.dp)
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
                        text = "Edit ${area.type.title}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = TextForestDeep
                    )
                    Text(
                        text = "Unit: ${unit.label} (${unit.symbol})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
            }

            // Save Area button
            Surface(
                onClick = { viewModel.saveActiveArea() },
                shape = RoundedCornerShape(16.dp),
                color = LiquidGrassPrimary,
                shadowElevation = 3.dp,
                modifier = Modifier.testTag("save_area_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 36.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Warning if over 100 meters
            if (isVeryLarge) {
                item {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFB45309))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "That is very large. Did you pick the right unit?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            )
                        }
                    }
                }
            }

            // Area Name & Note
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    LiquidSectionHeader(title = "Area Name & Notes")
                    Spacer(modifier = Modifier.height(8.dp))

                    LiquidTextField(
                        value = area.name,
                        onValueChange = { viewModel.updateActiveArea { a -> a.copy(name = it) } },
                        label = "Area name",
                        placeholder = "e.g. Main bedroom",
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        testTag = "area_name_input"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LiquidTextField(
                        value = area.note,
                        onValueChange = { viewModel.updateActiveArea { a -> a.copy(note = it) } },
                        label = "Area note (optional)",
                        placeholder = "e.g. Level concrete floor, sunny side",
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    )
                }
            }

            // Dimensions Card for Non-Bathroom Areas
            if (area.type != AreaType.BATHROOM) {
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        LiquidSectionHeader(
                            title = "Measurements (${unit.symbol})",
                            helper = "Every number field accepts dot or comma (3.5 or 3,5)"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (area.type == AreaType.WALL_ONLY) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.wallOnlyLengthInput,
                                        onValueChange = { viewModel.updateActiveArea { a -> a.copy(wallOnlyLengthInput = it) } },
                                        label = "Wall length",
                                        suffix = unit.symbol,
                                        keyboardType = KeyboardType.Decimal,
                                        testTag = "wall_length_input"
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.wallOnlyHeightInput,
                                        onValueChange = { viewModel.updateActiveArea { a -> a.copy(wallOnlyHeightInput = it) } },
                                        label = "Wall height",
                                        suffix = unit.symbol,
                                        keyboardType = KeyboardType.Decimal,
                                        testTag = "wall_height_input"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LiquidTextField(
                                value = area.wallOnlyOpeningsAreaInput,
                                onValueChange = { viewModel.updateActiveArea { a -> a.copy(wallOnlyOpeningsAreaInput = it) } },
                                label = "Openings to subtract (m²)",
                                placeholder = "0.0",
                                suffix = "m²",
                                keyboardType = KeyboardType.Decimal
                            )
                        } else {
                            // Room floor / Veranda base dimensions
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.lengthInput,
                                        onValueChange = { viewModel.updateActiveArea { a -> a.copy(lengthInput = it) } },
                                        label = "Length",
                                        suffix = unit.symbol,
                                        keyboardType = KeyboardType.Decimal,
                                        testTag = "room_length_input"
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.widthInput,
                                        onValueChange = { viewModel.updateActiveArea { a -> a.copy(widthInput = it) } },
                                        label = "Width",
                                        suffix = unit.symbol,
                                        keyboardType = KeyboardType.Decimal,
                                        testTag = "room_width_input"
                                    )
                                }
                            }

                            if (area.type == AreaType.ROOM_FLOOR) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LiquidSectionHeader(
                                    title = "Doorways",
                                    helper = "Subtracts door openings from skirting and trims"
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        LiquidTextField(
                                            value = area.doorwayCount.toString(),
                                            onValueChange = {
                                                val c = it.trim().toIntOrNull() ?: 0
                                                viewModel.updateActiveArea { a -> a.copy(doorwayCount = c) }
                                            },
                                            label = "Number of doors",
                                            placeholder = "1",
                                            keyboardType = KeyboardType.Number
                                        )
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        LiquidTextField(
                                            value = area.doorwayWidthInput,
                                            onValueChange = { viewModel.updateActiveArea { a -> a.copy(doorwayWidthInput = it) } },
                                            label = "Door width",
                                            suffix = unit.symbol,
                                            keyboardType = KeyboardType.Decimal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // BATHROOM SPECIFIC: 1. FLOOR TILES SECTION
            if (area.type == AreaType.BATHROOM) {
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        LiquidSectionHeader(
                            title = "Floor Tiles",
                            helper = "Enter floor dimensions and pick floor tile size"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                LiquidTextField(
                                    value = area.lengthInput,
                                    onValueChange = { viewModel.updateActiveArea { a -> a.copy(lengthInput = it) } },
                                    label = "Floor length",
                                    suffix = unit.symbol,
                                    keyboardType = KeyboardType.Decimal,
                                    testTag = "bathroom_floor_length_input"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                LiquidTextField(
                                    value = area.widthInput,
                                    onValueChange = { viewModel.updateActiveArea { a -> a.copy(widthInput = it) } },
                                    label = "Floor width",
                                    suffix = unit.symbol,
                                    keyboardType = KeyboardType.Decimal,
                                    testTag = "bathroom_floor_width_input"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                LiquidTextField(
                                    value = area.doorwayCount.toString(),
                                    onValueChange = {
                                        val c = it.trim().toIntOrNull() ?: 0
                                        viewModel.updateActiveArea { a -> a.copy(doorwayCount = c) }
                                    },
                                    label = "Number of doors",
                                    placeholder = "1",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                LiquidTextField(
                                    value = area.doorwayWidthInput,
                                    onValueChange = { viewModel.updateActiveArea { a -> a.copy(doorwayWidthInput = it) } },
                                    label = "Door width",
                                    suffix = unit.symbol,
                                    keyboardType = KeyboardType.Decimal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = LiquidGrassPale, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Floor tile size picker
                        TileSpecInlineEditor(
                            title = "Floor tile size:",
                            spec = area.bathroomFloorTileSpec,
                            presets = listOf(
                                Pair("30 x 30 cm", Pair(30.0, 30.0)),
                                Pair("40 x 40 cm", Pair(40.0, 40.0)),
                                Pair("60 x 60 cm", Pair(60.0, 60.0)),
                                Pair("60 x 120 cm", Pair(60.0, 120.0)),
                                Pair("80 x 80 cm", Pair(80.0, 80.0)),
                                Pair("Custom size", Pair(0.0, 0.0))
                            ),
                            onUpdate = { updated ->
                                viewModel.updateActiveArea { a -> a.copy(bathroomFloorTileSpec = updated) }
                            },
                            testTagPrefix = "bath_floor"
                        )
                    }
                }

                // BATHROOM SPECIFIC: 2. WALL TILES SECTION
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LiquidSectionHeader(
                                title = "Wall Tiles",
                                helper = "Wall length & height, or total wall area"
                            )
                            Switch(
                                checked = area.bathroomWall.enabled,
                                onCheckedChange = { en ->
                                    viewModel.updateActiveArea { a ->
                                        a.copy(bathroomWall = a.bathroomWall.copy(enabled = en))
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary),
                                modifier = Modifier.testTag("bathroom_tile_walls_switch")
                            )
                        }

                        if (area.bathroomWall.enabled) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Mode selection: Each wall vs Total wall area
                            Text(
                                text = "Wall entry method:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                BathroomWallEntryMode.values().forEach { mode ->
                                    LiquidPillChip(
                                        label = mode.label,
                                        isSelected = area.bathroomWall.entryMode == mode,
                                        onClick = {
                                            viewModel.updateActiveArea { a ->
                                                a.copy(bathroomWall = a.bathroomWall.copy(entryMode = mode))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (area.bathroomWall.entryMode == BathroomWallEntryMode.PER_WALL) {
                                Text(
                                    text = "Walls (${area.bathroomWall.walls.size} walls):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    area.bathroomWall.walls.forEachIndexed { idx, wall ->
                                        Surface(
                                            color = Color.White.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x20059669)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = wall.name,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
                                                    )
                                                    if (area.bathroomWall.walls.size > 1) {
                                                        IconButton(
                                                            onClick = {
                                                                viewModel.updateActiveArea { a ->
                                                                    val updatedList = a.bathroomWall.walls.filterIndexed { i, _ -> i != idx }
                                                                    a.copy(bathroomWall = a.bathroomWall.copy(walls = updatedList))
                                                                }
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.Close,
                                                                contentDescription = "Remove wall",
                                                                tint = MaterialTheme.colorScheme.error,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        LiquidTextField(
                                                            value = wall.lengthInput,
                                                            onValueChange = { newLen ->
                                                                viewModel.updateActiveArea { a ->
                                                                    val updated = a.bathroomWall.walls.toMutableList()
                                                                    updated[idx] = updated[idx].copy(lengthInput = newLen)
                                                                    a.copy(bathroomWall = a.bathroomWall.copy(walls = updated))
                                                                }
                                                            },
                                                            label = "Length",
                                                            suffix = "m",
                                                            keyboardType = KeyboardType.Decimal
                                                        )
                                                    }
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        LiquidTextField(
                                                            value = wall.heightInput,
                                                            onValueChange = { newH ->
                                                                viewModel.updateActiveArea { a ->
                                                                    val updated = a.bathroomWall.walls.toMutableList()
                                                                    updated[idx] = updated[idx].copy(heightInput = newH)
                                                                    a.copy(bathroomWall = a.bathroomWall.copy(walls = updated))
                                                                }
                                                            },
                                                            label = "Height",
                                                            suffix = "m",
                                                            keyboardType = KeyboardType.Decimal
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.updateActiveArea { a ->
                                                val nextNum = a.bathroomWall.walls.size + 1
                                                val defH = a.bathroomWall.walls.firstOrNull()?.heightInput ?: "2.4"
                                                val newWall = WallDimension(name = "Wall $nextNum", lengthInput = "3.0", heightInput = defH)
                                                a.copy(bathroomWall = a.bathroomWall.copy(walls = a.bathroomWall.walls + newWall))
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Add another wall", color = LiquidGrassPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // TOTAL AREA MODE
                                LiquidTextField(
                                    value = area.bathroomWall.totalWallAreaInput,
                                    onValueChange = { v ->
                                        viewModel.updateActiveArea { a ->
                                            a.copy(bathroomWall = a.bathroomWall.copy(totalWallAreaInput = v))
                                        }
                                    },
                                    label = "Total wall area (m²)",
                                    placeholder = "e.g. 24.5",
                                    suffix = "m²",
                                    keyboardType = KeyboardType.Decimal,
                                    testTag = "bathroom_total_wall_area_input"
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Openings to subtract from walls:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Doors row
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathroomWall.doorCount.toString(),
                                        onValueChange = {
                                            val c = it.trim().toIntOrNull() ?: 0
                                            viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(doorCount = c)) }
                                        },
                                        label = "Doors",
                                        keyboardType = KeyboardType.Number
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathroomWall.doorWidthM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(doorWidthM = v)) }
                                        },
                                        label = "Door width",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathroomWall.doorHeightM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(doorHeightM = v)) }
                                        },
                                        label = "Door height",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Windows row
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathroomWall.windowCount.toString(),
                                        onValueChange = {
                                            val c = it.trim().toIntOrNull() ?: 0
                                            viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(windowCount = c)) }
                                        },
                                        label = "Windows",
                                        keyboardType = KeyboardType.Number
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathroomWall.windowWidthM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(windowWidthM = v)) }
                                        },
                                        label = "Win width",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathroomWall.windowHeightM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(windowHeightM = v)) }
                                        },
                                        label = "Win height",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tiled up to the ceiling?", style = MaterialTheme.typography.bodyMedium, color = TextForestDeep)
                                Switch(
                                    checked = area.bathroomWall.tiledToCeiling,
                                    onCheckedChange = { c ->
                                        viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(tiledToCeiling = c)) }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = LiquidGrassPale, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Wall tile size picker (can be different from floor tile!)
                            TileSpecInlineEditor(
                                title = "Wall tile size (can be different from floor):",
                                spec = area.bathroomWallTileSpec,
                                presets = listOf(
                                    Pair("20 x 30 cm", Pair(20.0, 30.0)),
                                    Pair("25 x 40 cm", Pair(25.0, 40.0)),
                                    Pair("30 x 60 cm", Pair(60.0, 30.0)),
                                    Pair("30 x 30 cm", Pair(30.0, 30.0)),
                                    Pair("60 x 60 cm", Pair(60.0, 60.0)),
                                    Pair("Custom size", Pair(0.0, 0.0))
                                ),
                                onUpdate = { updated ->
                                    viewModel.updateActiveArea { a -> a.copy(bathroomWallTileSpec = updated) }
                                },
                                testTagPrefix = "bath_wall"
                            )
                        }
                    }
                }

                // Built-in Bath Tub
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LiquidSectionHeader(
                                title = "Is there a built-in bath tub?",
                                helper = "Tiled tub box with sides and top rim"
                            )
                            Switch(
                                checked = area.bathTub.enabled,
                                onCheckedChange = { en ->
                                    viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(enabled = en)) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
                            )
                        }

                        if (area.bathTub.enabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Tub dimensions (m):", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathTub.lengthM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(lengthM = v)) }
                                        },
                                        label = "Length",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathTub.widthM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(widthM = v)) }
                                        },
                                        label = "Width",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    LiquidTextField(
                                        value = area.bathTub.heightM.toString(),
                                        onValueChange = {
                                            val v = TileCalculatorEngine.parseNum(it)
                                            viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(heightM = v)) }
                                        },
                                        label = "Height",
                                        suffix = "m",
                                        keyboardType = KeyboardType.Decimal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tiled tub sides:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = area.bathTub.tileFront,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(tileFront = !a.bathTub.tileFront)) } },
                                    label = { Text("Front") }
                                )
                                FilterChip(
                                    selected = area.bathTub.tileBack,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(tileBack = !a.bathTub.tileBack)) } },
                                    label = { Text("Back") }
                                )
                                FilterChip(
                                    selected = area.bathTub.tileLeft,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(tileLeft = !a.bathTub.tileLeft)) } },
                                    label = { Text("Left") }
                                )
                                FilterChip(
                                    selected = area.bathTub.tileRight,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(bathTub = a.bathTub.copy(tileRight = !a.bathTub.tileRight)) } },
                                    label = { Text("Right") }
                                )
                            }
                        }
                    }
                }
            }

            // VERANDA SPECIFIC SECTIONS
            if (area.type == AreaType.VERANDA) {
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LiquidSectionHeader(
                                title = "Is the veranda raised like a box?",
                                helper = "Tiled side drops and vertical corners"
                            )
                            Switch(
                                checked = area.verandaRaised,
                                onCheckedChange = { r -> viewModel.updateActiveArea { a -> a.copy(verandaRaised = r) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
                            )
                        }

                        if (area.verandaRaised) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LiquidTextField(
                                value = area.verandaRaisedHeightInput,
                                onValueChange = { viewModel.updateActiveArea { a -> a.copy(verandaRaisedHeightInput = it) } },
                                label = "Height of raised edge",
                                suffix = unit.symbol,
                                keyboardType = KeyboardType.Decimal
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Exposed raised sides to tile:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = area.verandaSideFront,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(verandaSideFront = !a.verandaSideFront) } },
                                    label = { Text("Front") }
                                )
                                FilterChip(
                                    selected = area.verandaSideBack,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(verandaSideBack = !a.verandaSideBack) } },
                                    label = { Text("Back") }
                                )
                                FilterChip(
                                    selected = area.verandaSideLeft,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(verandaSideLeft = !a.verandaSideLeft) } },
                                    label = { Text("Left") }
                                )
                                FilterChip(
                                    selected = area.verandaSideRight,
                                    onClick = { viewModel.updateActiveArea { a -> a.copy(verandaSideRight = !a.verandaSideRight) } },
                                    label = { Text("Right") }
                                )
                            }
                        }
                    }
                }
            }

            // SKIRTING TILES SECTION
            val showSkirting = when (area.type) {
                AreaType.ROOM_FLOOR -> true
                AreaType.BATHROOM -> !area.bathroomWall.enabled // Only when walls NOT tiled
                AreaType.VERANDA -> true
                AreaType.WALL_ONLY -> false
            }

            if (showSkirting) {
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LiquidSectionHeader(
                                title = "Add skirting tiles?",
                                helper = "Tiles along the bottom perimeter"
                            )
                            Switch(
                                checked = area.skirting.enabled,
                                onCheckedChange = { en ->
                                    viewModel.updateActiveArea { a -> a.copy(skirting = a.skirting.copy(enabled = en)) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
                            )
                        }

                        if (area.skirting.enabled) {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Skirting Bed Thickness Control
                            Text("Skirting bed thickness:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val skirtingThicknessPresets = listOf(
                                    Pair(3.0, "Level 3 mm"),
                                    Pair(6.0, "Slightly uneven 6 mm"),
                                    Pair(10.0, "Very uneven 10 mm")
                                )
                                skirtingThicknessPresets.forEach { (thick, lbl) ->
                                    val isSel = area.skirting.bedThicknessMm == thick && area.skirting.customBedThicknessMm.isBlank()
                                    LiquidPillChip(
                                        label = lbl,
                                        isSelected = isSel,
                                        onClick = {
                                            viewModel.updateActiveArea { a ->
                                                a.copy(skirting = a.skirting.copy(bedThicknessMm = thick, customBedThicknessMm = ""))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            LiquidTextField(
                                value = if (area.skirting.customBedThicknessMm.isNotBlank()) area.skirting.customBedThicknessMm else area.skirting.bedThicknessMm.toString(),
                                onValueChange = { v ->
                                    val num = TileCalculatorEngine.parseNum(v)
                                    viewModel.updateActiveArea { a ->
                                        a.copy(skirting = a.skirting.copy(
                                            customBedThicknessMm = v,
                                            bedThicknessMm = if (num > 0) num else a.skirting.bedThicknessMm
                                        ))
                                    }
                                },
                                label = "Custom skirting bed thickness (mm):",
                                placeholder = "3",
                                suffix = "mm",
                                keyboardType = KeyboardType.Decimal
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Skirting height:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(7.0, 10.0, 15.0).forEach { h ->
                                    LiquidPillChip(
                                        label = "${h.toInt()} cm",
                                        isSelected = area.skirting.heightCm == h && area.skirting.customHeightCm.isBlank(),
                                        onClick = {
                                            viewModel.updateActiveArea { a ->
                                                a.copy(skirting = a.skirting.copy(heightCm = h, customHeightCm = ""))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            // Skirting length edit
                            LiquidTextField(
                                value = area.skirting.customLengthMeters,
                                onValueChange = { v ->
                                    viewModel.updateActiveArea { a -> a.copy(skirting = a.skirting.copy(customLengthMeters = v)) }
                                },
                                label = "Skirting length (meters):",
                                placeholder = "Change if different from room perimeter",
                                suffix = "m",
                                keyboardType = KeyboardType.Decimal
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("How is the skirting made?", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                LiquidPillChip(
                                    label = "Cut from floor tiles",
                                    isSelected = area.skirting.method == SkirtingMethod.CUT_FROM_FLOOR,
                                    onClick = {
                                        viewModel.updateActiveArea { a ->
                                            a.copy(skirting = a.skirting.copy(method = SkirtingMethod.CUT_FROM_FLOOR))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                LiquidPillChip(
                                    label = "Ready-made skirting",
                                    isSelected = area.skirting.method == SkirtingMethod.READY_MADE,
                                    onClick = {
                                        viewModel.updateActiveArea { a ->
                                            a.copy(skirting = a.skirting.copy(method = SkirtingMethod.READY_MADE))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // EDGE STRIPS SECTION
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LiquidSectionHeader(
                            title = "Edge strips (Trims)",
                            helper = "Finishing strip for doorways and edges"
                        )
                        Switch(
                            checked = area.edgeStrip.enabled,
                            onCheckedChange = { en ->
                                viewModel.updateActiveArea { a -> a.copy(edgeStrip = a.edgeStrip.copy(enabled = en)) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
                        )
                    }

                    if (area.edgeStrip.enabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Material:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LiquidPillChip(
                                label = "Plastic (Indoor)",
                                isSelected = area.edgeStrip.material == EdgeStripMaterial.PLASTIC,
                                onClick = {
                                    viewModel.updateActiveArea { a ->
                                        a.copy(edgeStrip = a.edgeStrip.copy(material = EdgeStripMaterial.PLASTIC))
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            LiquidPillChip(
                                label = "Metal (Outdoor/Veranda)",
                                isSelected = area.edgeStrip.material == EdgeStripMaterial.METAL,
                                onClick = {
                                    viewModel.updateActiveArea { a ->
                                        a.copy(edgeStrip = a.edgeStrip.copy(material = EdgeStripMaterial.METAL))
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LiquidTextField(
                            value = area.edgeStrip.customLengthMeters,
                            onValueChange = { v ->
                                viewModel.updateActiveArea { a -> a.copy(edgeStrip = a.edgeStrip.copy(customLengthMeters = v)) }
                            },
                            label = "Estimated edge length (meters):",
                            placeholder = "Leave empty to use automatic estimate",
                            suffix = "m",
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                }
            }

            // Save and Reset buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiquidPillButton(
                        text = "Save Area",
                        onClick = { viewModel.saveActiveArea() },
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        onClick = {
                            val idx = uiState.activeAreaIndex
                            if (idx != null) viewModel.resetArea(idx)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .height(54.dp)
                            .border(1.dp, Color(0x30059669), RoundedCornerShape(20.dp))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextForestMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TileSpecInlineEditor(
    title: String,
    spec: TileSpec,
    presets: List<Pair<String, Pair<Double, Double>>>,
    onUpdate: (TileSpec) -> Unit,
    testTagPrefix: String = "tile"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { (label, dims) ->
                val isSelected = spec.sizeLabel == label
                LiquidPillChip(
                    label = label,
                    isSelected = isSelected,
                    onClick = {
                        if (label == "Custom size") {
                            onUpdate(spec.copy(sizeLabel = label, isCustom = true))
                        } else {
                            onUpdate(
                                spec.copy(
                                    sizeLabel = label,
                                    lengthCm = dims.first,
                                    widthCm = dims.second,
                                    isCustom = false
                                )
                            )
                        }
                    }
                )
            }
        }

        if (spec.isCustom) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LiquidTextField(
                        value = spec.lengthCm.toString(),
                        onValueChange = {
                            val v = TileCalculatorEngine.parseNum(it)
                            onUpdate(spec.copy(lengthCm = v))
                        },
                        label = "Length (cm)",
                        placeholder = "30",
                        keyboardType = KeyboardType.Decimal
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    LiquidTextField(
                        value = spec.widthCm.toString(),
                        onValueChange = {
                            val v = TileCalculatorEngine.parseNum(it)
                            onUpdate(spec.copy(widthCm = v))
                        },
                        label = "Width (cm)",
                        placeholder = "30",
                        keyboardType = KeyboardType.Decimal
                    )
                }
            }
        }

        LiquidTextField(
            value = spec.tilesPerBox.toString(),
            onValueChange = {
                val c = it.trim().toIntOrNull() ?: 12
                onUpdate(spec.copy(tilesPerBox = c))
            },
            label = "Number of tiles in one box",
            placeholder = "12",
            suffix = "tiles / box",
            keyboardType = KeyboardType.Number,
            testTag = "${testTagPrefix}_tiles_per_box"
        )

        Text(
            text = "Waste allowance (cuts & breakage):",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5.0, 10.0, 15.0, 20.0).forEach { w ->
                LiquidPillChip(
                    label = "${w.toInt()}%",
                    isSelected = spec.wastePercent == w,
                    onClick = { onUpdate(spec.copy(wastePercent = w)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text(
            text = "Tile spacers size:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1.5, 2.0, 3.0, 4.0, 5.0, 6.0).forEach { sp ->
                LiquidPillChip(
                    label = "${if (sp == 1.5) "1.5" else sp.toInt().toString()} mm",
                    isSelected = spec.spacerMm == sp,
                    onClick = { onUpdate(spec.copy(spacerMm = sp)) }
                )
            }
        }

        Text(
            text = "Joint depth (tile thickness):",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextForestDeep)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(6.0, 8.0, 10.0, 12.0).forEach { depth ->
                LiquidPillChip(
                    label = "${depth.toInt()} mm",
                    isSelected = spec.jointDepthMm == depth,
                    onClick = { onUpdate(spec.copy(jointDepthMm = depth)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
