package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TileCalculatorUnitTest {

    private lateinit var viewModel: TileCalculatorViewModel

    @Before
    fun setUp() {
        viewModel = TileCalculatorViewModel()
    }

    @Test
    fun testDefaultCalculations() {
        // Room: 3.5m x 1.8m
        // Tile size: 60x60 cm (index 2)
        // Tiles per box: 12
        // Waste: 10%
        // Adhesive coverage: 4.0 m² / bag
        // Grout coverage: 10.0 m² / bag
        // Bonding coverage: 5.0 m² / L
        viewModel.onRoomLengthChange("3.5")
        viewModel.onRoomWidthChange("1.8")
        viewModel.onTileSizeSelect(2) // 60x60 cm
        viewModel.onTilesPerBoxChange("12")
        viewModel.onWasteAllowanceChange(10.0)
        viewModel.calculate()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        val result = state.result
        assertNotNull(result)

        // Room Area: 3.5 * 1.8 = 6.30 m²
        assertEquals(6.3, result!!.roomAreaSqM, 0.001)

        // Tile Area: 0.6 * 0.6 = 0.36 m²
        assertEquals(0.36, result.tileAreaSqM, 0.001)

        // Raw tiles = 6.3 / 0.36 = 17.5; with 10% waste = 19.25; ceil = 20 tiles
        assertEquals(20, result.totalTilesNeeded)

        // Boxes needed: ceil(20 / 12) = 2 boxes
        assertEquals(2, result.boxesToBuy)

        // Total tiles in 2 boxes = 24
        assertEquals(24, result.totalTilesInBoxes)

        // Spare tiles = 24 - 20 = 4
        assertEquals(4, result.spareTiles)

        // Adhesive: ceil(6.3 / 4.0) = 2 bags
        assertEquals(2, result.adhesiveBags)

        // Grout: ceil(6.3 / 10.0) = 1 bag
        assertEquals(1, result.groutBags)

        // Bonding liquid: ceil(6.3 / 5.0) = 2 litres
        assertEquals(2, result.bondingLitres)
    }

    @Test
    fun testCustomTileDimensions() {
        viewModel.onRoomLengthChange("4.0")
        viewModel.onRoomWidthChange("3.0")
        // Custom size is index 5
        viewModel.onTileSizeSelect(5)
        viewModel.onCustomTileLengthChange("50")
        viewModel.onCustomTileWidthChange("50")
        viewModel.onTilesPerBoxChange("10")
        viewModel.onWasteAllowanceChange(10.0)
        viewModel.calculate()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        val result = state.result
        assertNotNull(result)

        // Room area = 12.0 m²
        assertEquals(12.0, result!!.roomAreaSqM, 0.001)
        // Tile area = 0.5 * 0.5 = 0.25 m²
        assertEquals(0.25, result.tileAreaSqM, 0.001)
        // Tiles needed: 12.0 / 0.25 = 48 tiles * 1.10 = 52.8 -> ceil = 53
        assertEquals(53, result.totalTilesNeeded)
        // Boxes needed: ceil(53 / 10) = 6 boxes
        assertEquals(6, result.boxesToBuy)
        // Total tiles: 60
        assertEquals(60, result.totalTilesInBoxes)
        // Spare: 60 - 53 = 7
        assertEquals(7, result.spareTiles)
    }

    @Test
    fun testValidationForEmptyFields() {
        viewModel.onRoomLengthChange("")
        viewModel.calculate()
        assertEquals("Please enter your room length in meters (e.g. 3.5)", viewModel.uiState.value.errorMessage)

        viewModel.onRoomLengthChange("3.5")
        viewModel.onRoomWidthChange("")
        viewModel.calculate()
        assertEquals("Please enter your room width in meters (e.g. 1.8)", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun testReset() {
        viewModel.loadExample()
        assertNotNull(viewModel.uiState.value.result)
        viewModel.reset()
        assertNull(viewModel.uiState.value.result)
        assertEquals("", viewModel.uiState.value.roomLength)
        assertEquals("", viewModel.uiState.value.roomWidth)
    }
}
