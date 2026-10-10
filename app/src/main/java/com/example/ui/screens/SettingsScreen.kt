package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TileCalculatorViewModel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPillButton
import com.example.ui.components.LiquidSectionHeader
import com.example.ui.components.LiquidTextField
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var businessName by remember(uiState.userProfile) { mutableStateOf(uiState.userProfile.businessName) }
    var phone by remember(uiState.userProfile) { mutableStateOf(uiState.userProfile.phoneNumber) }

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
                .padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "My Details",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = TextForestDeep
                )
                Text(
                    text = "Reused automatically on quotations",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextForestMuted
                )
            }
        }

        LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
            LiquidSectionHeader(
                title = "Builder / Contractor Details",
                helper = "These details appear on the 'Prepared by' header of every written quotation"
            )
            Spacer(modifier = Modifier.height(14.dp))

            LiquidTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = "Your name or business name",
                placeholder = "e.g. Thabo Builders",
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                testTag = "settings_business_name_input"
            )

            Spacer(modifier = Modifier.height(10.dp))

            LiquidTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone number",
                placeholder = "e.g. 71 234 567",
                keyboardType = KeyboardType.Phone,
                testTag = "settings_phone_input"
            )

            Spacer(modifier = Modifier.height(18.dp))

            LiquidPillButton(
                text = "Save Details",
                onClick = {
                    viewModel.updateUserProfile(businessName.trim(), phone.trim())
                    Toast.makeText(context, "Details saved", Toast.LENGTH_SHORT).show()
                    viewModel.navigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "settings_save_button"
            )
        }
    }
}
