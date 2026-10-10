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

            // Dimensions Card
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
                        // Room floor / Bathroom / Veranda base dimensions
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

                        if (area.type == AreaType.ROOM_FLOOR || area.type == AreaType.BATHROOM) {
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

            // BATHROOM SPECIFIC SECTIONS
            if (area.type == AreaType.BATHROOM) {
                // Tiled Walls
                item {
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LiquidSectionHeader(
                                title = "Tile the walls too?",
                                helper = "Includes bathroom walls around perimeter"
                            )
                            Switch(
                                checked = area.bathroomWall.enabled,
                                onCheckedChange = { en ->
                                    viewModel.updateActiveArea { a ->
                                        a.copy(bathroomWall = a.bathroomWall.copy(enabled = en))
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiquidGrassPrimary)
                            )
                        }

                        if (area.bathroomWall.enabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LiquidTextField(
                                value = area.bathroomWall.wallHeightM.toString(),
                                onValueChange = {
                                    val v = TileCalculatorEngine.parseNum(it)
                                    viewModel.updateActiveArea { a -> a.copy(bathroomWall = a.bathroomWall.copy(wallHeightM = v)) }
                                },
                                label = "Wall height (m)",
                                suffix = "m",
                                keyboardType = KeyboardType.Decimal
                            )

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
