package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TileCalculatorViewModel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPillButton
import com.example.ui.components.LiquidPillChip
import com.example.ui.theme.*

@Composable
fun QuotationScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    BackHandler {
        viewModel.navigateBack()
    }

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
                    onClick = { viewModel.navigateBack() },
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
                        text = "Written Quotation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = TextForestDeep
                    )
                    Text(
                        text = "Copy or share via WhatsApp / SMS",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextForestMuted
                    )
                }
            }
        }

        // Format Switcher Pills: Detailed quotation vs Quick list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidPillChip(
                label = "Detailed quotation (for client)",
                isSelected = uiState.quotationDetailed,
                onClick = { viewModel.setQuotationDetailed(true) },
                modifier = Modifier.weight(1f)
            )
            LiquidPillChip(
                label = "Quick list (for shop)",
                isSelected = !uiState.quotationDetailed,
                onClick = { viewModel.setQuotationDetailed(false) },
                modifier = Modifier.weight(1f)
            )
        }

        // Editable Scrollable Quotation Text Box
        LiquidGlassCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "EDIT QUOTATION TEXT BEFORE SENDING:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp
                    ),
                    color = TextForestMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = uiState.quotationText,
                    onValueChange = { viewModel.updateQuotationText(it) },
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextForestDeep
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color(0x20059669), RoundedCornerShape(14.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0x0C059669),
                        focusedBorderColor = LiquidGrassPrimary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Buttons: Copy and Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Copy Button
            Surface(
                onClick = {
                    clipboardManager.setText(AnnotatedString(uiState.quotationText))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .border(1.dp, Color(0x30059669), RoundedCornerShape(20.dp))
                    .testTag("quotation_copy_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = null,
                        tint = LiquidGrassPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextForestDeep
                        )
                    )
                }
            }

            // Share Button
            LiquidPillButton(
                text = "Share",
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, uiState.activeJob?.jobName ?: "Tiling Quotation")
                        putExtra(Intent.EXTRA_TEXT, uiState.quotationText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Tiling Quotation via"))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.weight(1f),
                testTag = "quotation_share_button"
            )
        }
    }
}
