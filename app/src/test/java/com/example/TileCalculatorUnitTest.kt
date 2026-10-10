package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TileCalculatorUnitTest {

    private lateinit var viewModel: TileCalculatorViewModel

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = TileCalculatorViewModel(app)
    }

    @Test
    fun testCommaAndDotParsing() {
        assertEquals(3.5, TileCalculatorEngine.parseNum("3.5"), 0.001)
        assertEquals(3.5, TileCalculatorEngine.parseNum("3,5"), 0.001)
        assertEquals(12.75, TileCalculatorEngine.parseNum(" 12,75 "), 0.001)
    }

    @Test
    fun testUnitConversion() {
        // Meters to meters
        assertEquals(4.0, MeasurementUnit.METERS.toMeters(4.0), 0.001)
        // Centimeters to meters (400 cm -> 4.0 m)
        assertEquals(4.0, MeasurementUnit.CENTIMETERS.toMeters(400.0), 0.001)
        // Feet to meters (10 ft -> 3.048 m)
        assertEquals(3.048, MeasurementUnit.FEET.toMeters(10.0), 0.001)
    }

    @Test
    fun testSkirtingCutFromFloorTiles() {
        // Room: 4.0m x 3.0m
        // Doorway: 1 x 0.9m
        // Perimeter = 2*(4+3) - 0.9 = 13.1 m
        // Tile: 60x60 cm, skirting height 10 cm, waste 10%
        // Strips per tile = floor(60 / 10) = 6
        // Strip length = 0.6 m
        // Strips needed = ceil((13.1 / 0.6) * 1.10) = ceil(21.833 * 1.10) = ceil(24.016) = 25 strips
        // Floor tiles used = ceil(25 / 6) = 5 tiles
        val room = AreaItem(
            name = "Main bedroom",
            type = AreaType.ROOM_FLOOR,
            lengthInput = "4.0",
            widthInput = "3.0",
            doorwayCount = 1,
            doorwayWidthInput = "0.9",
            skirting = SkirtingConfig(enabled = true, heightCm = 10.0, method = SkirtingMethod.CUT_FROM_FLOOR)
        )

        val job = Job(
            unit = MeasurementUnit.METERS,
            isTileChosen = true,
            sharedFloorTileSpec = TileSpec(sizeLabel = "60 x 60 cm", lengthCm = 60.0, widthCm = 60.0, tilesPerBox = 12, wastePercent = 10.0, spacerMm = 3.0),
            areas = listOf(room)
        )

        val areaResult = TileCalculatorEngine.calculateArea(room, job)
        assertEquals(13.1, areaResult.skirtingLengthMeters, 0.001)
        assertEquals(5, areaResult.skirtingTilesAddedToFloor)

        // Floor surface: 4 * 3 = 12 m²
        // Eff tile area = (0.603 * 0.603) = 0.3636 m²
        // Raw floor tiles = 12 / 0.3636 = 33.00 -> with 10% waste = 36.3 -> ceil = 37 tiles
        assertEquals(37, areaResult.floorTilesOnly)

        // Total floor tiles needed = 37 + 5 = 42 tiles
        assertEquals(42, areaResult.totalFloorTilesNeeded)

        // Boxes: ceil(42 / 12) = 4 boxes
        assertEquals(4, areaResult.floorBoxesToBuy)
        // Total tiles in 4 boxes = 48 -> spare = 48 - 42 = 6 spare
        assertEquals(6, areaResult.floorSpareTiles)
    }

    @Test
    fun testAdhesiveCoverageFormula() {
        // Easy Grip: floor printed 3.5 m² at 5 mm bed, wall printed 5.0 m² at 3 mm bed
        // If floor bed is 12 mm (> 10 mm warning):
        val room = AreaItem(
            name = "Floor",
            type = AreaType.ROOM_FLOOR,
            lengthInput = "3.5",
            widthInput = "2.0", // 7.0 m²
            skirting = SkirtingConfig(enabled = false)
        )

        val job = Job(
            isTileChosen = true,
            areas = listOf(room),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                floorThicknessMm = 12.0
            )
        )

        val calc = TileCalculatorEngine.calculateJob(job)
        // 3.5 * (5 / 12) = 1.4583 m² per bag
        // 7.0 / 1.4583 = 4.8 -> ceil = 5 bags
        assertEquals(5, calc.adhesiveBags)
        assertTrue(calc.isBedThickWarning)
    }

    @Test
    fun testQuotationFormatHasNoPricesAndProperHeadings() {
        val room = AreaItem(name = "Lounge", type = AreaType.ROOM_FLOOR, lengthInput = "4.0", widthInput = "3.0")
        val job = Job(
            jobName = "Mr Kgosi - 3 bedroom house",
            clientName = "Mr Kgosi",
            siteAddress = "Gaborone",
            dateString = "10 Oct 2026",
            areas = listOf(room)
        )
        val profile = UserProfile("Thabo Builders", "71 234 567")

        val quote = TileCalculatorEngine.generateQuotation(job, profile, detailed = true)

        assertTrue(quote.contains("TILING QUOTATION"))
        assertTrue(quote.contains("Prepared by: Thabo Builders, 71 234 567"))
        assertTrue(quote.contains("Client: Mr Kgosi"))
        assertTrue(quote.contains("MATERIALS TO BUY"))
        assertTrue(quote.contains("NOTES"))

        // Golden rule: NO prices or currency anywhere in the app!
        assertFalse(quote.contains("BWP"))
        assertFalse(quote.contains("Pula"))
        assertFalse(quote.contains("$"))
        assertFalse(quote.contains("Cost"))
        assertFalse(quote.contains("Price"))
    }

    @Test
    fun testBondingLiquidSwitchedOffByDefault() {
        val adh = AdhesiveConfig()
        assertFalse(adh.includeBondingLiquid)
        assertEquals(BondingLiquidMode.ADDITIVE_SPLASH, adh.bondingLiquidMode)
        assertEquals(250.0, adh.bondingMlPerBag, 0.001)

        val room = AreaItem(name = "Room", type = AreaType.ROOM_FLOOR, lengthInput = "4.0", widthInput = "3.5")
        val job = Job(isTileChosen = true, areas = listOf(room), adhesiveConfig = adh)
        val calc = TileCalculatorEngine.calculateJob(job)
        assertEquals(0.0, calc.bondingLiquidLitres, 0.001)
        assertEquals("", calc.bondingLiquidResultText)
    }

    @Test
    fun testBondingLiquidSplashCalculationAndRounding() {
        // Room 4m x 3.5m = 14 m² -> Easy Grip 3.5m²/bag at 5mm = 4 bags of adhesive
        val room = AreaItem(name = "Room", type = AreaType.ROOM_FLOOR, lengthInput = "4.0", widthInput = "3.5", skirting = SkirtingConfig(enabled = false))

        // 1) 4 bags x 250 ml / 1000 = 1.0 litre -> rounded to nearest 0.25L = 1.0 litre
        val job1 = Job(
            isTileChosen = true,
            areas = listOf(room),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                includeBondingLiquid = true,
                bondingLiquidMode = BondingLiquidMode.ADDITIVE_SPLASH,
                bondingMlPerBag = 250.0
            )
        )
        val calc1 = TileCalculatorEngine.calculateJob(job1)
        assertEquals(4, calc1.adhesiveBags)
        assertEquals(1.0, calc1.bondingLiquidLitres, 0.001)
        assertEquals("Bonding liquid: 1 litre (about 250 ml per bag of adhesive)", calc1.bondingLiquidResultText)

        // 2) 1 bag x 150 ml / 1000 = 0.15 litre -> rounded UP to nearest 0.25L = 0.25 litres
        val smallRoom = AreaItem(name = "Pantry", type = AreaType.ROOM_FLOOR, lengthInput = "1.5", widthInput = "1.0", skirting = SkirtingConfig(enabled = false))
        val job2 = Job(
            isTileChosen = true,
            areas = listOf(smallRoom),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                includeBondingLiquid = true,
                bondingLiquidMode = BondingLiquidMode.ADDITIVE_SPLASH,
                bondingMlPerBag = 150.0
            )
        )
        val calc2 = TileCalculatorEngine.calculateJob(job2)
        assertEquals(1, calc2.adhesiveBags)
        assertEquals(0.25, calc2.bondingLiquidLitres, 0.001)
        assertEquals("Bonding liquid: 0.25 litres (about 150 ml per bag of adhesive)", calc2.bondingLiquidResultText)

        // 3) 3 bags x 400 ml / 1000 = 1.2 litres -> rounded UP to nearest 0.25L = 1.25 litres
        val medRoom = AreaItem(name = "Kitchen", type = AreaType.ROOM_FLOOR, lengthInput = "3.0", widthInput = "3.5", skirting = SkirtingConfig(enabled = false))
        val job3 = Job(
            isTileChosen = true,
            areas = listOf(medRoom),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                includeBondingLiquid = true,
                bondingLiquidMode = BondingLiquidMode.ADDITIVE_SPLASH,
                bondingMlPerBag = 400.0
            )
        )
        val calc3 = TileCalculatorEngine.calculateJob(job3)
        assertEquals(3, calc3.adhesiveBags)
        assertEquals(1.25, calc3.bondingLiquidLitres, 0.001)
        assertEquals("Bonding liquid: 1.25 litres (about 400 ml per bag of adhesive)", calc3.bondingLiquidResultText)

        // 4) Custom ml: 300 ml with 2 bags = 0.6 litres -> rounded UP to nearest 0.25L = 0.75 litres
        val twoBagRoom = AreaItem(name = "Foyer", type = AreaType.ROOM_FLOOR, lengthInput = "2.5", widthInput = "2.5", skirting = SkirtingConfig(enabled = false))
        val job4 = Job(
            isTileChosen = true,
            areas = listOf(twoBagRoom),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                includeBondingLiquid = true,
                bondingLiquidMode = BondingLiquidMode.ADDITIVE_SPLASH,
                customBondingMl = "300"
            )
        )
        val calc4 = TileCalculatorEngine.calculateJob(job4)
        assertEquals(2, calc4.adhesiveBags)
        assertEquals(0.75, calc4.bondingLiquidLitres, 0.001)
        assertEquals("Bonding liquid: 0.75 litres (about 300 ml per bag of adhesive)", calc4.bondingLiquidResultText)
    }

    @Test
    fun testBondingLiquidWetAreaCalculation() {
        // 4 bags of adhesive -> wet area mode replaces all mixing water: 4 x 5 = 20 litres
        val room = AreaItem(name = "Bathroom", type = AreaType.BATHROOM, lengthInput = "4.0", widthInput = "3.5", skirting = SkirtingConfig(enabled = false))
        val job = Job(
            isTileChosen = true,
            areas = listOf(room),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                includeBondingLiquid = true,
                bondingLiquidMode = BondingLiquidMode.WET_AREA_FULL
            )
        )
        val calc = TileCalculatorEngine.calculateJob(job)
        assertEquals(4, calc.adhesiveBags)
        assertEquals(20.0, calc.bondingLiquidLitres, 0.001)
        assertEquals("Bonding liquid: 20 litres (wet area: replaces all mixing water)", calc.bondingLiquidResultText)
    }

    @Test
    fun testBondingLiquidQuotationText() {
        val room = AreaItem(name = "Lounge", type = AreaType.ROOM_FLOOR, lengthInput = "4.0", widthInput = "3.5", skirting = SkirtingConfig(enabled = false))
        val jobSplash = Job(
            jobName = "Job Splash",
            clientName = "Mr Kgosi",
            isTileChosen = true,
            areas = listOf(room),
            adhesiveConfig = AdhesiveConfig(
                brand = AdhesiveBrand.EASY_GRIP,
                includeBondingLiquid = true,
                bondingLiquidMode = BondingLiquidMode.ADDITIVE_SPLASH,
                bondingMlPerBag = 250.0
            )
        )
        val quoteSplash = TileCalculatorEngine.generateQuotation(jobSplash, UserProfile(), detailed = false)
        assertTrue(quoteSplash.contains("Bonding liquid (optional): 1 litre (about 250 ml per bag of adhesive)"))

        val jobWet = jobSplash.copy(
            adhesiveConfig = jobSplash.adhesiveConfig.copy(bondingLiquidMode = BondingLiquidMode.WET_AREA_FULL)
        )
        val quoteWet = TileCalculatorEngine.generateQuotation(jobWet, UserProfile(), detailed = false)
        assertTrue(quoteWet.contains("Bonding liquid (wet area): 20 litres (replaces all mixing water, 5 litres per bag)"))
    }

    @Test
    fun testLegacyDefaultCalculations() {
        // Test compatibility helper methods
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
        assertEquals(6.3, result!!.roomAreaSqM, 0.001)
        assertEquals(20, result.totalTilesNeeded)
        assertEquals(2, result.boxesToBuy)
        assertEquals(4, result.spareTiles)
    }
}
