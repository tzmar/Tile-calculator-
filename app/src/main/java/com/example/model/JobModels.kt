package com.example.model

import java.util.UUID
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

enum class MeasurementUnit(val label: String, val symbol: String) {
    METERS("Meters", "m"),
    CENTIMETERS("Centimeters", "cm"),
    FEET("Feet", "ft");

    fun toMeters(value: Double): Double = when (this) {
        METERS -> value
        CENTIMETERS -> value * 0.01
        FEET -> value * 0.3048
    }

    fun fromMeters(meters: Double): Double = when (this) {
        METERS -> meters
        CENTIMETERS -> meters * 100.0
        FEET -> meters / 0.3048
    }
}

enum class AreaType(val title: String, val description: String) {
    ROOM_FLOOR("Room floor", "Floor with optional doorways & skirting"),
    BATHROOM("Bathroom", "Floor with optional tiled walls & built-in bath tub"),
    VERANDA("Veranda", "Outdoor floor with optional raised box sides"),
    WALL_ONLY("Wall only", "Feature walls or splashbacks with openings")
}

enum class SkirtingMethod(val label: String) {
    CUT_FROM_FLOOR("Cut from the floor tiles"),
    READY_MADE("Ready-made skirting tiles")
}

enum class EdgeStripMaterial(val label: String) {
    PLASTIC("Plastic"),
    METAL("Metal")
}

enum class AdhesiveBrand(
    val displayName: String,
    val defaultFloorCov: Double,
    val defaultWallCov: Double,
    val bagWeightKg: Int
) {
    EASY_GRIP("Easy Grip Contractors (Buildezee, 20 kg)", 3.5, 5.0, 20),
    TILEMATE("TileMate (20 kg)", 4.0, 6.0, 20),
    OTHER("Other brand", 3.5, 5.0, 20)
}

data class TileSpec(
    val sizeLabel: String = "60 x 60 cm",
    val lengthCm: Double = 60.0,
    val widthCm: Double = 60.0,
    val isCustom: Boolean = false,
    val tilesPerBox: Int = 12,
    val wastePercent: Double = 10.0,
    val spacerMm: Double = 3.0
)

data class SkirtingConfig(
    val enabled: Boolean = true,
    val heightCm: Double = 10.0,
    val customHeightCm: String = "",
    val customLengthMeters: String = "", // User override if edited
    val method: SkirtingMethod = SkirtingMethod.CUT_FROM_FLOOR,
    val readyMadePieceLenCm: Double = 60.0,
    val readyMadePiecesPerBox: Int = 10,
    val finishWithEdgeStrip: Boolean = true
)

data class EdgeStripConfig(
    val enabled: Boolean = true,
    val material: EdgeStripMaterial = EdgeStripMaterial.PLASTIC,
    val customLengthMeters: String = "", // User override if edited
    val pieceLengthMeters: Double = 2.5,
    val tileThicknessMm: Int = 10
)

data class BathTubConfig(
    val enabled: Boolean = false,
    val lengthM: Double = 1.7,
    val widthM: Double = 0.75,
    val heightM: Double = 0.55,
    val tileFront: Boolean = true,
    val tileBack: Boolean = false,
    val tileLeft: Boolean = true,
    val tileRight: Boolean = true,
    val tileTopRim: Boolean = true,
    val hollowLengthM: Double = 1.4,
    val hollowWidthM: Double = 0.55
)

data class BathroomWallConfig(
    val enabled: Boolean = false,
    val wallHeightM: Double = 2.1,
    val tiledToCeiling: Boolean = false,
    val doorCount: Int = 1,
    val doorWidthM: Double = 0.8,
    val doorHeightM: Double = 2.0,
    val windowCount: Int = 0,
    val windowWidthM: Double = 0.6,
    val windowHeightM: Double = 0.6
)

data class AreaItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Main room",
    val type: AreaType = AreaType.ROOM_FLOOR,
    val note: String = "",

    // Room Floor / Bathroom / Veranda base dimensions (stored in current Unit or meters)
    val lengthInput: String = "4.0",
    val widthInput: String = "3.0",

    // Doorways (for room floor or bathroom floor)
    val doorwayCount: Int = 0,
    val doorwayWidthInput: String = "0.9",

    // Bathroom specifics
    val bathroomWall: BathroomWallConfig = BathroomWallConfig(),
    val bathTub: BathTubConfig = BathTubConfig(),

    // Veranda specifics
    val verandaRaised: Boolean = true,
    val verandaRaisedHeightInput: String = "0.3",
    val verandaSideFront: Boolean = true,
    val verandaSideBack: Boolean = false,
    val verandaSideLeft: Boolean = true,
    val verandaSideRight: Boolean = true,
    val verandaTrimVerticalCorners: Boolean = true,

    // Wall Only specifics
    val wallOnlyLengthInput: String = "4.0",
    val wallOnlyHeightInput: String = "2.4",
    val wallOnlyOpeningsAreaInput: String = "0.0",

    // Skirting and Edge Strips
    val skirting: SkirtingConfig = SkirtingConfig(),
    val edgeStrip: EdgeStripConfig = EdgeStripConfig(),

    // Per-area tile override if not using shared tile
    val customTileSpec: TileSpec? = null
)

data class AdhesiveConfig(
    val brand: AdhesiveBrand = AdhesiveBrand.EASY_GRIP,
    val otherFloorCoverage: Double = 3.5,
    val otherWallCoverage: Double = 5.0,
    val floorThicknessMm: Double = 5.0,
    val wallThicknessMm: Double = 3.0,
    val groutCoveragePerBag: Double = 10.0,
    val includeBondingLiquid: Boolean = true
)

data class Job(
    val id: String = UUID.randomUUID().toString(),
    val jobName: String = "",
    val clientName: String = "",
    val siteAddress: String = "",
    val notes: String = "",
    val dateString: String = "",
    val unit: MeasurementUnit = MeasurementUnit.METERS,
    val areas: List<AreaItem> = emptyList(),

    // Tile choice
    val isTileChosen: Boolean = false,
    val sharedFloorTileSpec: TileSpec = TileSpec(),
    val sameTileForAllAreas: Boolean = true,
    val sameTileForWallsAndSides: Boolean = true,
    val sharedWallTileSpec: TileSpec = TileSpec(sizeLabel = "30 x 60 cm", lengthCm = 60.0, widthCm = 30.0, tilesPerBox = 8),

    // Materials configs
    val adhesiveConfig: AdhesiveConfig = AdhesiveConfig()
)

data class UserProfile(
    val businessName: String = "",
    val phoneNumber: String = ""
)
