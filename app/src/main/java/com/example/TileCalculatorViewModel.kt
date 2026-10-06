package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.ceil

data class TileSizeOption(
    val label: String,
    val lengthCm: Double,
    val widthCm: Double,
    val isCustom: Boolean = false,
    val description: String = ""
)

data class TileCalculationResult(
    val roomAreaSqM: Double,
    val tileAreaSqM: Double,
    val totalTilesNeeded: Int,
    val tilesPerBox: Int,
    val boxesToBuy: Int,
    val totalTilesInBoxes: Int,
    val spareTiles: Int,
    val adhesiveBags: Int,
    val groutBags: Int,
    val bondingLitres: Int,
    val wastePercent: Double,
    val tileLengthCm: Double,
    val tileWidthCm: Double
)

data class TileCalculatorUiState(
    val roomLength: String = "",
    val roomWidth: String = "",
    val selectedTileSizeIndex: Int = 2, // 60x60 cm by default
    val customTileLength: String = "",
    val customTileWidth: String = "",
    val tilesPerBox: String = "12",
    val wasteAllowance: Double = 10.0,
    val isAdvancedOpen: Boolean = false,
    val adhesiveCoverage: String = "4.0",
    val groutCoverage: String = "10.0",
    val bondingCoverage: String = "5.0",
    val errorMessage: String? = null,
    val result: TileCalculationResult? = null
)

class TileCalculatorViewModel : ViewModel() {

    val commonTileSizes = listOf(
        TileSizeOption("30 x 30 cm", 30.0, 30.0, description = "Common for bathrooms & mosaics"),
        TileSizeOption("40 x 40 cm", 40.0, 40.0, description = "Great for medium rooms"),
        TileSizeOption("60 x 60 cm", 60.0, 60.0, description = "Most popular floor tile"),
        TileSizeOption("60 x 120 cm", 60.0, 120.0, description = "Large format rectangular"),
        TileSizeOption("80 x 80 cm", 80.0, 80.0, description = "Extra-large modern tile"),
        TileSizeOption("Custom size", 0.0, 0.0, isCustom = true, description = "Type custom width & length")
    )

    private val _uiState = MutableStateFlow(TileCalculatorUiState())
    val uiState: StateFlow<TileCalculatorUiState> = _uiState.asStateFlow()

    fun onRoomLengthChange(value: String) {
        _uiState.update { it.copy(roomLength = value, errorMessage = null) }
    }

    fun onRoomWidthChange(value: String) {
        _uiState.update { it.copy(roomWidth = value, errorMessage = null) }
    }

    fun onTileSizeSelect(index: Int) {
        _uiState.update { it.copy(selectedTileSizeIndex = index, errorMessage = null) }
    }

    fun onCustomTileLengthChange(value: String) {
        _uiState.update { it.copy(customTileLength = value, errorMessage = null) }
    }

    fun onCustomTileWidthChange(value: String) {
        _uiState.update { it.copy(customTileWidth = value, errorMessage = null) }
    }

    fun onTilesPerBoxChange(value: String) {
        _uiState.update { it.copy(tilesPerBox = value, errorMessage = null) }
    }

    fun onWasteAllowanceChange(value: Double) {
        _uiState.update { it.copy(wasteAllowance = value) }
    }

    fun toggleAdvancedSettings() {
        _uiState.update { it.copy(isAdvancedOpen = !it.isAdvancedOpen) }
    }

    fun onAdhesiveCoverageChange(value: String) {
        _uiState.update { it.copy(adhesiveCoverage = value, errorMessage = null) }
    }

    fun onGroutCoverageChange(value: String) {
        _uiState.update { it.copy(groutCoverage = value, errorMessage = null) }
    }

    fun onBondingCoverageChange(value: String) {
        _uiState.update { it.copy(bondingCoverage = value, errorMessage = null) }
    }

    fun loadExample() {
        _uiState.update {
            it.copy(
                roomLength = "3.5",
                roomWidth = "1.8",
                selectedTileSizeIndex = 2, // 60x60 cm
                tilesPerBox = "12",
                wasteAllowance = 10.0,
                adhesiveCoverage = "4.0",
                groutCoverage = "10.0",
                bondingCoverage = "5.0",
                errorMessage = null
            )
        }
        calculate()
    }

    fun reset() {
        _uiState.value = TileCalculatorUiState()
    }

    fun calculate() {
        val state = _uiState.value

        // Validation with plain, friendly everyday language
        val length = state.roomLength.trim().toDoubleOrNull()
        if (length == null || length <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter your room length in meters (e.g. 3.5)") }
            return
        }

        val width = state.roomWidth.trim().toDoubleOrNull()
        if (width == null || width <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter your room width in meters (e.g. 1.8)") }
            return
        }

        val selectedOption = commonTileSizes.getOrElse(state.selectedTileSizeIndex) { commonTileSizes[2] }
        val tileLenCm: Double
        val tileWidthCm: Double

        if (selectedOption.isCustom) {
            val customLen = state.customTileLength.trim().toDoubleOrNull()
            val customWid = state.customTileWidth.trim().toDoubleOrNull()
            if (customLen == null || customLen <= 0 || customWid == null || customWid <= 0) {
                _uiState.update { it.copy(errorMessage = "Please enter your custom tile dimensions in cm (e.g. 50 x 50)") }
                return
            }
            tileLenCm = customLen
            tileWidthCm = customWid
        } else {
            tileLenCm = selectedOption.lengthCm
            tileWidthCm = selectedOption.widthCm
        }

        val boxCount = state.tilesPerBox.trim().toIntOrNull()
        if (boxCount == null || boxCount <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter how many tiles come in one box (e.g. 12)") }
            return
        }

        val adhesiveCov = state.adhesiveCoverage.trim().toDoubleOrNull() ?: 4.0
        val groutCov = state.groutCoverage.trim().toDoubleOrNull() ?: 10.0
        val bondingCov = state.bondingCoverage.trim().toDoubleOrNull() ?: 5.0

        if (adhesiveCov <= 0 || groutCov <= 0 || bondingCov <= 0) {
            _uiState.update { it.copy(errorMessage = "Material coverage values must be greater than zero") }
            return
        }

        // Calculations strictly per requirements:
        // Room area = length x width
        val roomArea = length * width

        // Tile area = tile length x tile width (convert cm to meters)
        val tileArea = (tileLenCm / 100.0) * (tileWidthCm / 100.0)

        // Tiles needed = room area / tile area, then add waste allowance, round UP
        val rawTilesNeeded = (roomArea / tileArea) * (1.0 + (state.wasteAllowance / 100.0))
        val totalTilesNeeded = ceil(rawTilesNeeded).toInt()

        // Boxes needed = tiles needed / tiles per box, always rounded UP
        val boxesNeeded = ceil(totalTilesNeeded.toDouble() / boxCount).toInt()

        // Spare tiles = (boxes x tiles per box) minus tiles needed
        val totalTilesInBoxes = boxesNeeded * boxCount
        val spareTiles = totalTilesInBoxes - totalTilesNeeded

        // Adhesive bags = room area / coverage per bag, rounded UP
        val adhesiveBags = ceil(roomArea / adhesiveCov).toInt()

        // Grout bags = room area / coverage per bag, rounded UP
        val groutBags = ceil(roomArea / groutCov).toInt()

        // Bonding liquid litres = room area / coverage per litre, rounded UP
        val bondingLitres = ceil(roomArea / bondingCov).toInt()

        val calcResult = TileCalculationResult(
            roomAreaSqM = roomArea,
            tileAreaSqM = tileArea,
            totalTilesNeeded = totalTilesNeeded,
            tilesPerBox = boxCount,
            boxesToBuy = boxesNeeded,
            totalTilesInBoxes = totalTilesInBoxes,
            spareTiles = spareTiles,
            adhesiveBags = adhesiveBags,
            groutBags = groutBags,
            bondingLitres = bondingLitres,
            wastePercent = state.wasteAllowance,
            tileLengthCm = tileLenCm,
            tileWidthCm = tileWidthCm
        )

        _uiState.update {
            it.copy(
                errorMessage = null,
                result = calcResult
            )
        }
    }
}
