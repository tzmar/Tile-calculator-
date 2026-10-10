package com.example

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.*

@Composable
fun TileCalculatorScreen(
    viewModel: TileCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

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
        Crossfade(targetState = uiState.currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                AppScreen.JOBS_LIST -> JobsListScreen(viewModel = viewModel)
                AppScreen.JOB_DETAIL -> JobDetailScreen(viewModel = viewModel)
                AppScreen.AREA_EDITOR -> AreaEditorScreen(viewModel = viewModel)
                AppScreen.QUOTATION_VIEW -> QuotationScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
