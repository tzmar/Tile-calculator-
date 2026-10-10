package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppScreen
import com.example.TileCalculatorViewModel
import com.example.model.Job
import com.example.model.TileCalculatorEngine
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPillButton
import com.example.ui.components.LiquidTextField
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun JobsListScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

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
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = LiquidGrassPrimary.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(LiquidGrassBright, LiquidGrassPrimary, LiquidGrassDeep)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = "Tile Icon",
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
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = TextForestDeep
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = LiquidGrassMint.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = LiquidGrassDeep,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Botswana & Africa Job Estimator",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextForestMuted
                    )
                }
            }

            // Settings button (My details)
            Surface(
                onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("settings_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = LiquidGrassPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Info Banner
        if (uiState.infoMessage != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = LiquidGrassPale,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.infoMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextForestDeep
                    )
                    IconButton(
                        onClick = { viewModel.dismissInfoMessage() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = TextForestDeep,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Action: New Job Button (Big pill)
        LiquidPillButton(
            text = "+ New Job",
            onClick = { viewModel.createNewJob() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            testTag = "new_job_button"
        )

        // Section Title: My Jobs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MY SAVED JOBS (${uiState.jobs.size})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                ),
                color = TextForestMuted
            )
        }

        // Jobs List
        if (uiState.jobs.isEmpty()) {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.SquareFoot,
                        contentDescription = null,
                        tint = LiquidGrassPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No saved jobs yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextForestDeep
                    )
                    Text(
                        text = "Tap '+ New Job' to record measurements on site. You can pick tiles later at the shop!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.jobs, key = { it.id }) { job ->
                    JobCardItem(
                        job = job,
                        onOpen = { viewModel.openJob(job) },
                        onRename = { viewModel.requestRenameJob(job) },
                        onDuplicate = { viewModel.duplicateJob(job) },
                        onDelete = { viewModel.requestDeleteJob(job) }
                    )
                }
            }
        }
    }

    // Rename Dialog
    if (uiState.jobToRename != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRenameDialog() },
            title = { Text("Rename Job") },
            text = {
                LiquidTextField(
                    value = uiState.renameInput,
                    onValueChange = { viewModel.onRenameInputChange(it) },
                    placeholder = "e.g. Mr Kgosi - 3 bedroom house",
                    label = "Job name",
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmRenameJob() }) {
                    Text("Rename", color = LiquidGrassDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRenameDialog() }) {
                    Text("Cancel", color = TextForestMuted)
                }
            }
        )
    }

    // Confirm Delete Dialog
    if (uiState.jobToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteJobDialog() },
            title = { Text("Delete Job?") },
            text = {
                Text("Are you sure you want to delete '${uiState.jobToDelete?.jobName}'? This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteJob() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteJobDialog() }) {
                    Text("Cancel", color = TextForestMuted)
                }
            }
        )
    }
}

@Composable
private fun JobCardItem(
    job: Job,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val calc = remember(job) { TileCalculatorEngine.calculateJob(job) }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("job_card_${job.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Name & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.jobName.ifBlank { "Untitled Job" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = TextForestDeep
                    )
                    if (job.clientName.isNotBlank() || job.siteAddress.isNotBlank()) {
                        val clientSite = listOf(job.clientName, job.siteAddress).filter { it.isNotBlank() }.joinToString(" · ")
                        Text(
                            text = clientSite,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextForestMuted
                        )
                    }
                }

                // Status chip: "Measured" vs "Tile chosen"
                Surface(
                    color = if (job.isTileChosen) LiquidGrassPrimary else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (job.isTileChosen) "Tile chosen" else "Measured",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (job.isTileChosen) Color.White else Color(0xFF92400E)
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // 3-dots menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Options",
                            tint = TextForestMuted
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open") },
                            leadingIcon = { Icon(Icons.Filled.FolderOpen, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onOpen()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate") },
                            leadingIcon = { Icon(Icons.Outlined.ContentCopy, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // Metrics row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${job.areas.size} ${if (job.areas.size == 1) "area" else "areas"}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = TextForestMuted
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f m² total", calc.grandTotalAreaSqM),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = LiquidGrassDeep
                )
                if (job.isTileChosen) {
                    Text(
                        text = "${calc.totalFloorBoxes} boxes",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = LiquidGrassPrimary
                    )
                } else {
                    Text(
                        text = "Tile at shop",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextForestMuted)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (job.dateString.isNotBlank()) {
                    Text(
                        text = job.dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
            }
        }
    }
}
