package com.example

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassSpecularHighlight
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.LiquidGrassAura
import com.example.ui.theme.LiquidGrassBright
import com.example.ui.theme.LiquidGrassDeep
import com.example.ui.theme.LiquidGrassMint
import com.example.ui.theme.LiquidGrassPale
import com.example.ui.theme.LiquidGrassPrimary
import com.example.ui.theme.TextForestDeep
import com.example.ui.theme.TextForestMuted
import com.example.ui.theme.TextPureWhite
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileCalculatorScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            coroutineScope.launch {
                listState.animateScrollToItem(index = 7)
            }
        }
    }

    // Liquid Grass ambient background with organic dewdrops and fluid aura
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF7FDF9),
                        Color(0xFFECFDF5),
                        Color(0xFFE2F9EC)
                    )
                )
            )
            .drawBehind {
                // Liquid grass ambient glows
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x3534D399), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.10f),
                        radius = size.width * 0.55f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2810B981), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.45f),
                        radius = size.width * 0.60f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x20059669), Color.Transparent),
                        center = Offset(size.width * 0.30f, size.height * 0.85f),
                        radius = size.width * 0.65f
                    )
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // iOS Frosted Glass Top Navigation Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.82f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Liquid Glass Icon Squircle
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = LiquidGrassPrimary.copy(alpha = 0.3f))
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            LiquidGrassBright,
                                            LiquidGrassPrimary,
                                            LiquidGrassDeep
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.linearGradient(
                                        listOf(Color.White.copy(alpha = 0.8f), Color.Transparent)
                                    ),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = "Tile Grass Icon",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Tile Calculator",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 21.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = TextForestDeep
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // iOS Capsule badge
                                Surface(
                                    color = LiquidGrassMint.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "LIQUID",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.8.sp
                                        ),
                                        color = LiquidGrassDeep,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Lush & simple tile estimation",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = TextForestMuted
                            )
                        }
                    }

                    // iOS Reset Pill Button
                    Surface(
                        onClick = { viewModel.reset() },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("reset_top_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Reset inputs",
                                tint = LiquidGrassPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            // Main Scrollable Body
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
            ) {
                // iOS Frosted Example Banner
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(LiquidGrassPale),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lightbulb,
                                        contentDescription = "Tip",
                                        tint = LiquidGrassPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Quick Starter Room",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = TextForestDeep
                                    )
                                    Text(
                                        text = "3.5m × 1.8m bathroom example",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextForestMuted
                                    )
                                }
                            }

                            // iOS Pill Button
                            Surface(
                                onClick = { viewModel.loadExample() },
                                shape = RoundedCornerShape(20.dp),
                                color = LiquidGrassPrimary,
                                shadowElevation = 3.dp,
                                modifier = Modifier.testTag("try_example_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Try Example",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Banner
                if (uiState.errorMessage != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFEE2E2).copy(alpha = 0.95f),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(20.dp))
                                .testTag("error_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WarningAmber,
                                    contentDescription = "Error",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF991B1B)
                                    )
                                )
                            }
                        }
                    }
                }

                // iOS Grouped Inset Card: ROOM MEASUREMENTS
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            IosSectionHeader(
                                number = "1",
                                title = "Room Length",
                                helper = "Measure the longest wall of your room from corner to corner"
                            )

                            IosTextField(
                                value = uiState.roomLength,
                                onValueChange = { viewModel.onRoomLengthChange(it) },
                                placeholder = "e.g. 3.5",
                                suffix = "meters",
                                testTag = "room_length_input"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            IosSectionHeader(
                                number = "2",
                                title = "Room Width",
                                helper = "Measure across the other wall from corner to corner"
                            )

                            IosTextField(
                                value = uiState.roomWidth,
                                onValueChange = { viewModel.onRoomWidthChange(it) },
                                placeholder = "e.g. 1.8",
                                suffix = "meters",
                                testTag = "room_width_input"
                            )
                        }
                    }
                }

                // iOS Grouped Inset Card: TILE SPECIFICATIONS
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            IosSectionHeader(
                                number = "3",
                                title = "Tile Size",
                                helper = "Select a standard size or enter custom dimensions"
                            )

                            // iOS Segmented Tile Size Selector & Dropdown
                            IosTileSizeSegmented(
                                viewModel = viewModel,
                                uiState = uiState
                            )

                            // Hint: "600x600 mm = 60x60 cm"
                            Surface(
                                color = LiquidGrassPale.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lightbulb,
                                        contentDescription = null,
                                        tint = LiquidGrassDeep,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Hint: 600×600 mm = 60×60 cm",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        ),
                                        color = LiquidGrassDeep
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            IosSectionHeader(
                                number = "4",
                                title = "Tiles Per Box",
                                helper = "Look on the side of the box in the store"
                            )

                            IosTextField(
                                value = uiState.tilesPerBox,
                                onValueChange = { viewModel.onTilesPerBoxChange(it) },
                                placeholder = "e.g. 12",
                                suffix = "tiles / box",
                                keyboardType = KeyboardType.Number,
                                testTag = "tiles_per_box_input"
                            )
                        }
                    }
                }

                // iOS Grouped Inset Card: WASTE ALLOWANCE & ADVANCED
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IosSectionHeader(
                                    number = "5",
                                    title = "Waste Allowance",
                                    helper = "Extra tiles for cuts and breakages",
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    color = LiquidGrassPrimary,
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 2.dp
                                ) {
                                    Text(
                                        text = "${uiState.wasteAllowance.toInt()}% extra",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // iOS Segmented Chips for Waste
                            IosWasteSelector(
                                wastePercent = uiState.wasteAllowance,
                                onWasteChange = { viewModel.onWasteAllowanceChange(it) }
                            )

                            // 6. Advanced settings accordion
                            IosAdvancedSettingsAccordion(
                                isOpen = uiState.isAdvancedOpen,
                                onToggle = { viewModel.toggleAdvancedSettings() },
                                adhesiveCoverage = uiState.adhesiveCoverage,
                                onAdhesiveChange = { viewModel.onAdhesiveCoverageChange(it) },
                                groutCoverage = uiState.groutCoverage,
                                onGroutChange = { viewModel.onGroutCoverageChange(it) },
                                bondingCoverage = uiState.bondingCoverage,
                                onBondingChange = { viewModel.onBondingCoverageChange(it) }
                            )
                        }
                    }
                }

                // Primary Calculate Button (Juicy Liquid Grass pill with gloss)
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.calculate() },
                            shape = RoundedCornerShape(22.dp),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .testTag("calculate_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                LiquidGrassBright,
                                                LiquidGrassPrimary,
                                                LiquidGrassDeep
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(Color.White.copy(alpha = 0.7f), Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(22.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Gloss highlight
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .fillMaxWidth(0.9f)
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.8f))
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Calculate,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Calculate Tile & Materials",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            letterSpacing = (-0.3).sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        // Secondary actions: Reset & Copy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                onClick = { viewModel.reset() },
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White.copy(alpha = 0.85f),
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .border(1.dp, GlassCardBorder, RoundedCornerShape(18.dp))
                                    .testTag("reset_button")
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        tint = TextForestDeep,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Reset All",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextForestDeep
                                        )
                                    )
                                }
                            }

                            if (uiState.result != null) {
                                Surface(
                                    onClick = {
                                        val summary = buildShoppingListText(uiState.result!!)
                                        clipboardManager.setText(AnnotatedString(summary))
                                        Toast.makeText(context, "Shopping list copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color.White.copy(alpha = 0.85f),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .border(1.dp, GlassCardBorder, RoundedCornerShape(18.dp))
                                        .testTag("copy_shopping_list_button")
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = null,
                                            tint = LiquidGrassPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Copy List",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = LiquidGrassPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // RESULTS SECTION (Liquid Grass Hero Capsule & iOS Cards)
                if (uiState.result != null) {
                    val res = uiState.result!!

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Text(
                                text = "CALCULATION RESULTS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = TextForestMuted
                            )
                        }
                    }

                    // 1. BOXES TO BUY - LIQUID GRASS HERO CAPSULE (Most prominent)
                    item {
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
                            // Glossy top liquid reflection
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .fillMaxWidth(0.92f)
                                    .height(2.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color.Transparent, Color.White.copy(alpha = 0.8f), Color.Transparent)
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 26.dp),
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

                                Text(
                                    text = "${res.boxesToBuy}",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 72.sp,
                                        lineHeight = 76.sp,
                                        color = Color.White
                                    )
                                )

                                Text(
                                    text = if (res.boxesToBuy == 1) "BOX OF TILES" else "BOXES OF TILES",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = Color(0xFFD1FAE5)
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Frosted Explanation Pill
                                Surface(
                                    color = Color.White.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.3f),
                                        RoundedCornerShape(16.dp)
                                    )
                                ) {
                                    Text(
                                        text = "You'll use ${res.totalTilesNeeded} of the ${res.totalTilesInBoxes} tiles, so you'll have ${res.spareTiles} spare.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        ),
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Room Area & Tiles Needed (iOS Split Glass Cards)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Room Area
                            GlassCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("room_area_result_card")
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(LiquidGrassPale),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.SquareFoot,
                                                contentDescription = null,
                                                tint = LiquidGrassPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Room Area",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = TextForestMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.2f m²", res.roomAreaSqM),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 24.sp
                                        ),
                                        color = TextForestDeep
                                    )
                                    Text(
                                        text = "Floor size",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextForestMuted
                                    )
                                }
                            }

                            // Tiles Needed
                            GlassCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tiles_needed_result_card")
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(LiquidGrassPale),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ShoppingBag,
                                                contentDescription = null,
                                                tint = LiquidGrassPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Tiles Needed",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = TextForestMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "${res.totalTilesNeeded}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 24.sp
                                        ),
                                        color = TextForestDeep
                                    )
                                    Text(
                                        text = "With ${res.wastePercent.toInt()}% buffer",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextForestMuted
                                    )
                                }
                            }
                        }
                    }

                    // Materials Grouped Inset Section
                    item {
                        Text(
                            text = "MATERIALS NEEDED TO BUY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = TextForestMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IosMaterialItemRow(
                                    icon = Icons.Outlined.Handyman,
                                    title = "Tile Adhesive / Cement",
                                    quantity = "${res.adhesiveBags} ${if (res.adhesiveBags == 1) "bag" else "bags"}",
                                    subtitle = "Standard 25 kg bags (covers 4 m² each)",
                                    testTag = "adhesive_result_card"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(LiquidGrassPale.copy(alpha = 0.8f))
                                )

                                IosMaterialItemRow(
                                    icon = Icons.Outlined.CheckCircle,
                                    title = "Tile Grout",
                                    quantity = "${res.groutBags} ${if (res.groutBags == 1) "bag" else "bags"}",
                                    subtitle = "Standard 5 kg bags (covers 10 m² each)",
                                    testTag = "grout_result_card"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(LiquidGrassPale.copy(alpha = 0.8f))
                                )

                                IosMaterialItemRow(
                                    icon = Icons.Outlined.WaterDrop,
                                    title = "Bonding Liquid",
                                    quantity = "${res.bondingLitres} ${if (res.bondingLitres == 1) "litre" else "litres"}",
                                    subtitle = "For floor prep & primer (covers 5 m² / L)",
                                    testTag = "bonding_result_card"
                                )
                            }
                        }
                    }
                }

                // iOS Footnote Disclaimer
                item {
                    Surface(
                        color = Color.White.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                            .padding(top = 4.dp)
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
                                text = "Estimates only. Check the coverage on your product's packaging.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = TextForestMuted
                                )
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

// Reusable iOS Frosted Glass Card
@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color(0x1A059669)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.95f),
                        LiquidGrassMint.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
    ) {
        content()
    }
}

// iOS Section Header Component
@Composable
private fun IosSectionHeader(
    number: String,
    title: String,
    helper: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = LiquidGrassPrimary,
                shape = CircleShape,
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = (-0.3).sp
                ),
                color = TextForestDeep
            )
        }
        Text(
            text = helper,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                lineHeight = 17.sp
            ),
            color = TextForestMuted,
            modifier = Modifier.padding(start = 28.dp)
        )
    }
}

// iOS Inset Rounded Text Field
@Composable
private fun IosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suffix: String,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = TextForestMuted.copy(alpha = 0.5f)
            )
        },
        trailingIcon = {
            Surface(
                color = LiquidGrassPale.copy(alpha = 0.8f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LiquidGrassDeep
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0x0C059669),
            focusedBorderColor = LiquidGrassPrimary,
            unfocusedBorderColor = Color(0x20059669),
            focusedTextColor = TextForestDeep,
            unfocusedTextColor = TextForestDeep
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

// 3. iOS Tile Size Segmented + Dropdown Selector
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IosTileSizeSegmented(
    viewModel: TileCalculatorViewModel,
    uiState: TileCalculatorUiState
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val selectedOption = viewModel.commonTileSizes.getOrElse(uiState.selectedTileSizeIndex) {
        viewModel.commonTileSizes[2]
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Horizontal iOS Segmented Scroll Pill Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.commonTileSizes.forEachIndexed { index, option ->
                val isSelected = (index == uiState.selectedTileSizeIndex)
                Surface(
                    onClick = { viewModel.onTileSizeSelect(index) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) LiquidGrassPrimary else Color.White.copy(alpha = 0.8f),
                    shadowElevation = if (isSelected) 3.dp else 0.dp,
                    modifier = Modifier.border(
                        1.dp,
                        if (isSelected) LiquidGrassPrimary else Color(0x25059669),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.White else TextForestDeep
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }
        }

        // Dropdown fallback / full selector trigger
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                onClick = { dropdownExpanded = true },
                shape = RoundedCornerShape(16.dp),
                color = Color(0x0C059669),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x20059669), RoundedCornerShape(16.dp))
                    .testTag("tile_size_dropdown")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selected: ${selectedOption.label}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextForestDeep
                        )
                        if (selectedOption.description.isNotEmpty()) {
                            Text(
                                text = selectedOption.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextForestMuted
                            )
                        }
                    }
                    Icon(
                        imageVector = if (dropdownExpanded) Icons.Filled.ExpandLess else Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = LiquidGrassPrimary
                    )
                }
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                viewModel.commonTileSizes.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (index == uiState.selectedTileSizeIndex) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (option.description.isNotEmpty()) {
                                    Text(
                                        text = option.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextForestMuted
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            if (index == uiState.selectedTileSizeIndex) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = LiquidGrassPrimary
                                )
                            }
                        },
                        onClick = {
                            viewModel.onTileSizeSelect(index)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Custom size expandable fields
        AnimatedVisibility(
            visible = selectedOption.isCustom,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(LiquidGrassPale.copy(alpha = 0.5f))
                    .border(1.dp, LiquidGrassMint.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Custom Tile Dimensions (cm):",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextForestDeep
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.customTileLength,
                        onValueChange = { viewModel.onCustomTileLengthChange(it) },
                        label = { Text("Length (cm)") },
                        placeholder = { Text("50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_tile_length_input")
                    )

                    OutlinedTextField(
                        value = uiState.customTileWidth,
                        onValueChange = { viewModel.onCustomTileWidthChange(it) },
                        label = { Text("Width (cm)") },
                        placeholder = { Text("50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_tile_width_input")
                    )
                }
            }
        }
    }
}

// 5. iOS Waste Selector
@Composable
private fun IosWasteSelector(
    wastePercent: Double,
    onWasteChange: (Double) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Segmented bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf(
                Pair(5.0, "5%"),
                Pair(10.0, "10% Rec."),
                Pair(15.0, "15% Cuts"),
                Pair(20.0, "20%")
            )
            presets.forEach { (value, label) ->
                val isSelected = (wastePercent == value)
                Surface(
                    onClick = { onWasteChange(value) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) LiquidGrassPrimary else Color.White.copy(alpha = 0.8f),
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .border(
                            1.dp,
                            if (isSelected) LiquidGrassPrimary else Color(0x25059669),
                            RoundedCornerShape(14.dp)
                        )
                        .testTag("waste_chip_${value.toInt()}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else TextForestDeep
                            )
                        )
                    }
                }
            }
        }

        // Liquid Slider
        Slider(
            value = wastePercent.toFloat(),
            onValueChange = { onWasteChange(it.toDouble()) },
            valueRange = 0f..30f,
            steps = 29,
            colors = SliderDefaults.colors(
                thumbColor = LiquidGrassPrimary,
                activeTrackColor = LiquidGrassBright,
                inactiveTrackColor = LiquidGrassPale
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("waste_slider")
        )
    }
}

// 6. iOS Advanced Settings Accordion
@Composable
private fun IosAdvancedSettingsAccordion(
    isOpen: Boolean,
    onToggle: () -> Unit,
    adhesiveCoverage: String,
    onAdhesiveChange: (String) -> Unit,
    groutCoverage: String,
    onGroutChange: (String) -> Unit,
    bondingCoverage: String,
    onBondingChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0x0C059669),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x20059669), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(LiquidGrassPale),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = null,
                            tint = LiquidGrassPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Advanced settings",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextForestDeep
                            )
                        )
                        Text(
                            text = "Packaging coverage estimates",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextForestMuted
                        )
                    }
                }
                Icon(
                    imageVector = if (isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isOpen) "Collapse" else "Expand",
                    tint = LiquidGrassPrimary
                )
            }

            AnimatedVisibility(
                visible = isOpen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IosTextField(
                        value = adhesiveCoverage,
                        onValueChange = onAdhesiveChange,
                        placeholder = "4.0",
                        suffix = "m² / 25kg bag",
                        testTag = "adhesive_coverage_input"
                    )

                    IosTextField(
                        value = groutCoverage,
                        onValueChange = onGroutChange,
                        placeholder = "10.0",
                        suffix = "m² / 5kg bag",
                        testTag = "grout_coverage_input"
                    )

                    IosTextField(
                        value = bondingCoverage,
                        onValueChange = onBondingChange,
                        placeholder = "5.0",
                        suffix = "m² / litre",
                        testTag = "bonding_coverage_input"
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

// iOS Material Item Row
@Composable
private fun IosMaterialItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    quantity: String,
    subtitle: String,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(LiquidGrassPale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LiquidGrassPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextForestDeep
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = TextForestMuted
            )
        }

        Surface(
            color = LiquidGrassPrimary,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Text(
                text = quantity,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

private fun buildShoppingListText(res: TileCalculationResult): String {
    return """
Tile Calculator (Liquid Edition):
---------------------------------
• Floor Area: ${String.format(Locale.getDefault(), "%.2f", res.roomAreaSqM)} m²
• Tile Size: ${res.tileLengthCm.toInt()} x ${res.tileWidthCm.toInt()} cm
• Boxes to Buy: ${res.boxesToBuy} boxes (${res.tilesPerBox} tiles per box)
• Total Tiles: ${res.totalTilesInBoxes} (Need ${res.totalTilesNeeded}, leaving ${res.spareTiles} spare)
• Tile Adhesive (Cement): ${res.adhesiveBags} bag(s) (25 kg)
• Tile Grout: ${res.groutBags} bag(s) (5 kg)
• Bonding Liquid: ${res.bondingLitres} litre(s)
---------------------------------
Calculated with Tile Calculator
""".trimIndent()
}
