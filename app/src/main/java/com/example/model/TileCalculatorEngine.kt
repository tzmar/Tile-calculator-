package com.example.model

import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class AreaCalculatedResult(
    val areaId: String,
    val areaName: String,
    val areaType: AreaType,
    val floorAreaSqM: Double,
    val wallAreaSqM: Double,
    val totalAreaSqM: Double,

    // Tiles (when tile chosen)
    val isTileChosen: Boolean,
    val floorTilesOnly: Int,
    val skirtingTilesAddedToFloor: Int,
    val totalFloorTilesNeeded: Int,
    val floorBoxesToBuy: Int,
    val totalFloorTilesInBoxes: Int,
    val floorSpareTiles: Int,

    // Separate wall tiles (if separate wall tile configured)
    val hasSeparateWallTile: Boolean,
    val wallTilesNeeded: Int,
    val wallBoxesToBuy: Int,
    val totalWallTilesInBoxes: Int,
    val wallSpareTiles: Int,

    // Skirting
    val skirtingEnabled: Boolean,
    val skirtingLengthMeters: Double,
    val skirtingHeightCm: Double,
    val skirtingMethod: SkirtingMethod,
    val readyMadeSkirtingPieces: Int,
    val readyMadeSkirtingBoxes: Int,

    // Edge strips for this area
    val edgeStripEnabled: Boolean,
    val edgeStripMaterial: EdgeStripMaterial,
    val edgeStripLengthMeters: Double,
    val edgeStripPieces: Int,
    val edgeStripThicknessMm: Int,
    val edgeStripPieceLengthM: Double,

    // Spacers for this area
    val spacersCount: Int,
    val spacerSizeMm: Double
)

data class JobCalculatedResult(
    val totalFloorAreaSqM: Double,
    val totalWallAreaSqM: Double,
    val grandTotalAreaSqM: Double,

    // Tiles
    val isTileChosen: Boolean,
    val totalFloorBoxes: Int,
    val totalFloorTilesInBoxes: Int,
    val totalFloorTilesNeeded: Int,
    val totalFloorSpareTiles: Int,
    val floorTileSizeLabel: String,
    val floorTilesPerBox: Int,

    // Wall tiles (if separate)
    val hasSeparateWallTiles: Boolean,
    val totalWallBoxes: Int,
    val totalWallTilesInBoxes: Int,
    val totalWallTilesNeeded: Int,
    val totalWallSpareTiles: Int,
    val wallTileSizeLabel: String,
    val wallTilesPerBox: Int,

    // Ready-made skirting
    val totalReadyMadeSkirtingBoxes: Int,
    val totalReadyMadeSkirtingPieces: Int,

    // Adhesive
    val adhesiveBags: Int,
    val adhesiveBrandName: String,
    val floorThicknessMm: Double,
    val wallThicknessMm: Double,
    val floorCoveragePerBag: Double,
    val wallCoveragePerBag: Double,
    val isBedThickWarning: Boolean, // > 10 mm
    val bondingLiquidLitres: Int,

    // Grout
    val groutBags: Int,

    // Spacers
    val totalSpacersCount: Int,
    val spacerSizeMm: Double,

    // Edge strips
    val plasticEdgeStripLengthM: Double,
    val plasticEdgeStripPieces: Int,
    val plasticTileThicknessMm: Int,

    val metalEdgeStripLengthM: Double,
    val metalEdgeStripPieces: Int,
    val metalTileThicknessMm: Int,

    // Area results breakdown
    val areaResults: List<AreaCalculatedResult>
)

object TileCalculatorEngine {

    fun parseNum(s: String): Double {
        return s.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
    }

    fun calculateArea(area: AreaItem, job: Job): AreaCalculatedResult {
        val unit = job.unit

        val lengthM = unit.toMeters(parseNum(area.lengthInput))
        val widthM = unit.toMeters(parseNum(area.widthInput))
        val doorWidthM = unit.toMeters(parseNum(area.doorwayWidthInput).let { if (it <= 0) 0.9 else it })

        var floorArea = 0.0
        var wallArea = 0.0
        var skirtingLenEst = 0.0
        var edgeStripLenEst = 0.0

        when (area.type) {
            AreaType.ROOM_FLOOR -> {
                floorArea = lengthM * widthM
                // Skirting estimate = room perimeter minus doorway widths
                skirtingLenEst = max(0.0, 2.0 * (lengthM + widthM) - (area.doorwayCount * doorWidthM))
                // Edge strips = doorways x door width + skirting top edge (if on)
                edgeStripLenEst = (area.doorwayCount * doorWidthM)
            }

            AreaType.BATHROOM -> {
                floorArea = lengthM * widthM
                // Walls
                if (area.bathroomWall.enabled) {
                    val wHeight = area.bathroomWall.wallHeightM
                    val rawWallArea = 2.0 * (lengthM + widthM) * wHeight
                    val doorOpenings = area.bathroomWall.doorCount * area.bathroomWall.doorWidthM * area.bathroomWall.doorHeightM
                    val windowOpenings = area.bathroomWall.windowCount * area.bathroomWall.windowWidthM * area.bathroomWall.windowHeightM
                    wallArea += max(0.0, rawWallArea - (doorOpenings + windowOpenings))
                }

                // Bath tub
                if (area.bathTub.enabled) {
                    val tub = area.bathTub
                    var tubTiledSideLen = 0.0
                    if (tub.tileFront) tubTiledSideLen += tub.lengthM
                    if (tub.tileBack) tubTiledSideLen += tub.lengthM
                    if (tub.tileLeft) tubTiledSideLen += tub.widthM
                    if (tub.tileRight) tubTiledSideLen += tub.widthM

                    val tubSidesArea = tubTiledSideLen * tub.heightM
                    wallArea += tubSidesArea // tub sides are WALL surfaces

                    if (tub.tileTopRim) {
                        val hollowArea = tub.hollowLengthM * tub.hollowWidthM
                        val tubTopArea = max(0.0, (tub.lengthM * tub.widthM) - hollowArea)
                        floorArea += tubTopArea // tub top rim is FLOOR surface
                    }
                }

                // Skirting: Allowed only when bathroom walls are NOT tiled
                if (!area.bathroomWall.enabled) {
                    skirtingLenEst = max(0.0, 2.0 * (lengthM + widthM) - (area.doorwayCount * doorWidthM))
                }

                // Edge strip estimation
                var bathEdge = area.doorwayCount * doorWidthM
                if (area.bathroomWall.enabled && !area.bathroomWall.tiledToCeiling) {
                    // top edge of wall tiles
                    bathEdge += max(0.0, 2.0 * (lengthM + widthM) - (area.bathroomWall.doorCount * area.bathroomWall.doorWidthM))
                }
                if (area.bathroomWall.enabled) {
                    bathEdge += area.bathroomWall.windowCount * 2.0 * (area.bathroomWall.windowWidthM + area.bathroomWall.windowHeightM)
                }
                if (area.bathTub.enabled) {
                    var tubEdgeLen = 0.0
                    if (area.bathTub.tileFront) tubEdgeLen += area.bathTub.lengthM
                    if (area.bathTub.tileBack) tubEdgeLen += area.bathTub.lengthM
                    if (area.bathTub.tileLeft) tubEdgeLen += area.bathTub.widthM
                    if (area.bathTub.tileRight) tubEdgeLen += area.bathTub.widthM
                    bathEdge += tubEdgeLen
                }
                edgeStripLenEst = bathEdge
            }

            AreaType.VERANDA -> {
                floorArea = lengthM * widthM
                var verandaExposedSideLen = 0.0
                if (area.verandaRaised) {
                    val rHeight = unit.toMeters(parseNum(area.verandaRaisedHeightInput).let { if (it <= 0) 0.3 else it })
                    if (area.verandaSideFront) verandaExposedSideLen += lengthM
                    if (area.verandaSideBack) verandaExposedSideLen += lengthM
                    if (area.verandaSideLeft) verandaExposedSideLen += widthM
                    if (area.verandaSideRight) verandaExposedSideLen += widthM
                    wallArea += verandaExposedSideLen * rHeight
                }
                // Veranda skirting: side against the house
                skirtingLenEst = lengthM

                // Veranda edge strip: top outer edge + vertical corners
                var vEdge = verandaExposedSideLen
                if (area.verandaRaised && area.verandaTrimVerticalCorners) {
                    val rHeight = unit.toMeters(parseNum(area.verandaRaisedHeightInput).let { if (it <= 0) 0.3 else it })
                    var corners = 0
                    if (area.verandaSideFront && area.verandaSideLeft) corners++
                    if (area.verandaSideFront && area.verandaSideRight) corners++
                    if (area.verandaSideBack && area.verandaSideLeft) corners++
                    if (area.verandaSideBack && area.verandaSideRight) corners++
                    vEdge += corners * rHeight
                }
                edgeStripLenEst = vEdge
            }

            AreaType.WALL_ONLY -> {
                val wallL = unit.toMeters(parseNum(area.wallOnlyLengthInput))
                val wallH = unit.toMeters(parseNum(area.wallOnlyHeightInput))
                val openings = parseNum(area.wallOnlyOpeningsAreaInput)
                wallArea = max(0.0, (wallL * wallH) - openings)
                floorArea = 0.0
                skirtingLenEst = 0.0
                edgeStripLenEst = wallL // top edge
            }
        }

        // Skirting actual length
        val skirtingActive = when (area.type) {
            AreaType.ROOM_FLOOR -> area.skirting.enabled
            AreaType.BATHROOM -> !area.bathroomWall.enabled && area.skirting.enabled
            AreaType.VERANDA -> area.skirting.enabled
            AreaType.WALL_ONLY -> false
        }

        val actualSkirtingLengthM = if (skirtingActive) {
            val userOverride = parseNum(area.skirting.customLengthMeters)
            if (userOverride > 0) userOverride else skirtingLenEst
        } else 0.0

        // If skirting is finished with edge strip, add to edge strips
        if (skirtingActive && area.skirting.finishWithEdgeStrip) {
            edgeStripLenEst += actualSkirtingLengthM
        }

        val actualEdgeStripLengthM = if (area.edgeStrip.enabled) {
            val userOverride = parseNum(area.edgeStrip.customLengthMeters)
            if (userOverride > 0) userOverride else edgeStripLenEst
        } else 0.0

        // Skirting wall area (Skirting counts as WALL surface)
        val skirtingHeightCm = if (area.skirting.customHeightCm.isNotBlank()) {
            parseNum(area.skirting.customHeightCm).let { if (it <= 0) area.skirting.heightCm else it }
        } else {
            area.skirting.heightCm
        }
        val skirtingWallAreaSqM = if (skirtingActive) {
            actualSkirtingLengthM * (skirtingHeightCm / 100.0)
        } else 0.0

        val totalAreaWithSkirting = floorArea + wallArea + skirtingWallAreaSqM

        // Tiles calculation
        val floorTileSpec = if (job.sameTileForAllAreas) job.sharedFloorTileSpec else (area.customTileSpec ?: job.sharedFloorTileSpec)
        val useSeparateWallTile = !job.sameTileForWallsAndSides && wallArea > 0
        val wallTileSpec = if (useSeparateWallTile) job.sharedWallTileSpec else floorTileSpec

        var floorTilesOnly = 0
        var skirtingTilesAddedToFloor = 0
        var totalFloorTilesNeeded = 0
        var floorBoxesToBuy = 0
        var totalFloorTilesInBoxes = 0
        var floorSpareTiles = 0

        var wallTilesNeeded = 0
        var wallBoxesToBuy = 0
        var totalWallTilesInBoxes = 0
        var wallSpareTiles = 0

        var readyMadePieces = 0
        var readyMadeBoxes = 0
        var spacersCount = 0

        if (job.isTileChosen) {
            val spacerM = floorTileSpec.spacerMm / 1000.0
            val effFloorTileL = (floorTileSpec.lengthCm / 100.0) + spacerM
            val effFloorTileW = (floorTileSpec.widthCm / 100.0) + spacerM
            val effFloorTileArea = max(0.0001, effFloorTileL * effFloorTileW)

            // Floor surface tiles
            val rawFloorTiles = floorArea / effFloorTileArea
            floorTilesOnly = ceil(rawFloorTiles * (1.0 + floorTileSpec.wastePercent / 100.0)).toInt()

            // If walls use the SAME tile and not separate, we can add wall tiles or separate them
            val sameTileWallTiles = if (!useSeparateWallTile && wallArea > 0) {
                val rawWallTiles = wallArea / effFloorTileArea
                ceil(rawWallTiles * (1.0 + floorTileSpec.wastePercent / 100.0)).toInt()
            } else 0

            // Skirting calculation
            if (skirtingActive && actualSkirtingLengthM > 0) {
                if (area.skirting.method == SkirtingMethod.CUT_FROM_FLOOR) {
                    val stripsPerTile = max(1, floor(floorTileSpec.widthCm / max(1.0, skirtingHeightCm)).toInt())
                    val stripLenM = floorTileSpec.lengthCm / 100.0
                    val stripsNeeded = ceil((actualSkirtingLengthM / stripLenM) * (1.0 + floorTileSpec.wastePercent / 100.0)).toInt()
                    skirtingTilesAddedToFloor = ceil(stripsNeeded.toDouble() / stripsPerTile).toInt()
                } else {
                    // Ready made
                    val pieceLenM = max(0.1, area.skirting.readyMadePieceLenCm / 100.0)
                    readyMadePieces = ceil((actualSkirtingLengthM / pieceLenM) * 1.10).toInt()
                    readyMadeBoxes = ceil(readyMadePieces.toDouble() / max(1, area.skirting.readyMadePiecesPerBox)).toInt()
                }
            }

            totalFloorTilesNeeded = floorTilesOnly + skirtingTilesAddedToFloor + sameTileWallTiles
            val boxCapacity = max(1, floorTileSpec.tilesPerBox)
            floorBoxesToBuy = ceil(totalFloorTilesNeeded.toDouble() / boxCapacity).toInt()
            totalFloorTilesInBoxes = floorBoxesToBuy * boxCapacity
            floorSpareTiles = totalFloorTilesInBoxes - totalFloorTilesNeeded

            // Separate wall tile calculation
            if (useSeparateWallTile) {
                val wallSpacerM = wallTileSpec.spacerMm / 1000.0
                val effWallTileL = (wallTileSpec.lengthCm / 100.0) + wallSpacerM
                val effWallTileW = (wallTileSpec.widthCm / 100.0) + wallSpacerM
                val effWallTileArea = max(0.0001, effWallTileL * effWallTileW)
                val rawWallTiles = wallArea / effWallTileArea
                wallTilesNeeded = ceil(rawWallTiles * (1.0 + wallTileSpec.wastePercent / 100.0)).toInt()
                val wallBoxCapacity = max(1, wallTileSpec.tilesPerBox)
                wallBoxesToBuy = ceil(wallTilesNeeded.toDouble() / wallBoxCapacity).toInt()
                totalWallTilesInBoxes = wallBoxesToBuy * wallBoxCapacity
                wallSpareTiles = totalWallTilesInBoxes - wallTilesNeeded
            }

            // Spacers for this area
            if (floorArea > 0 && lengthM > 0 && widthM > 0) {
                val tilesAlong = ceil(lengthM / effFloorTileL)
                val tilesAcross = ceil(widthM / effFloorTileW)
                val rectSpacers = ceil((tilesAlong + 1) * (tilesAcross + 1) * 1.10).toInt()
                spacersCount += rectSpacers
            }
            if (wallArea > 0) {
                val tileAreaToUse = if (useSeparateWallTile) {
                    val wSpacerM = wallTileSpec.spacerMm / 1000.0
                    ((wallTileSpec.lengthCm / 100.0) + wSpacerM) * ((wallTileSpec.widthCm / 100.0) + wSpacerM)
                } else effFloorTileArea
                val tilesBeforeWaste = wallArea / max(0.0001, tileAreaToUse)
                val wallSpacers = ceil((tilesBeforeWaste + 2.0 * sqrt(tilesBeforeWaste) + 1.0) * 1.10).toInt()
                spacersCount += wallSpacers
            }
        }

        // Edge strip pieces for this area
        val stripPieceLen = if (area.edgeStrip.pieceLengthMeters > 0) area.edgeStrip.pieceLengthMeters else 2.5
        val areaEdgePieces = if (actualEdgeStripLengthM > 0) {
            ceil((actualEdgeStripLengthM * 1.10) / stripPieceLen).toInt()
        } else 0

        return AreaCalculatedResult(
            areaId = area.id,
            areaName = area.name.ifBlank { area.type.title },
            areaType = area.type,
            floorAreaSqM = floorArea,
            wallAreaSqM = wallArea + skirtingWallAreaSqM,
            totalAreaSqM = totalAreaWithSkirting,
            isTileChosen = job.isTileChosen,
            floorTilesOnly = floorTilesOnly,
            skirtingTilesAddedToFloor = skirtingTilesAddedToFloor,
            totalFloorTilesNeeded = totalFloorTilesNeeded,
            floorBoxesToBuy = floorBoxesToBuy,
            totalFloorTilesInBoxes = totalFloorTilesInBoxes,
            floorSpareTiles = floorSpareTiles,
            hasSeparateWallTile = useSeparateWallTile,
            wallTilesNeeded = wallTilesNeeded,
            wallBoxesToBuy = wallBoxesToBuy,
            totalWallTilesInBoxes = totalWallTilesInBoxes,
            wallSpareTiles = wallSpareTiles,
            skirtingEnabled = skirtingActive,
            skirtingLengthMeters = actualSkirtingLengthM,
            skirtingHeightCm = skirtingHeightCm,
            skirtingMethod = area.skirting.method,
            readyMadeSkirtingPieces = readyMadePieces,
            readyMadeSkirtingBoxes = readyMadeBoxes,
            edgeStripEnabled = area.edgeStrip.enabled,
            edgeStripMaterial = area.edgeStrip.material,
            edgeStripLengthMeters = actualEdgeStripLengthM,
            edgeStripPieces = areaEdgePieces,
            edgeStripThicknessMm = area.edgeStrip.tileThicknessMm,
            edgeStripPieceLengthM = stripPieceLen,
            spacersCount = spacersCount,
            spacerSizeMm = floorTileSpec.spacerMm
        )
    }

    fun calculateJob(job: Job): JobCalculatedResult {
        val areaResults = job.areas.map { calculateArea(it, job) }

        var totalFloorArea = 0.0
        var totalWallArea = 0.0
        var totalFloorBoxes = 0
        var totalFloorTilesNeeded = 0
        var totalFloorTilesInBoxes = 0

        var hasSeparateWallTiles = false
        var totalWallBoxes = 0
        var totalWallTilesNeeded = 0
        var totalWallTilesInBoxes = 0

        var totalReadyMadeBoxes = 0
        var totalReadyMadePieces = 0

        var totalSpacers = 0

        var plasticLength = 0.0
        var metalLength = 0.0
        var plasticThickness = 10
        var metalThickness = 10

        areaResults.forEach { res ->
            totalFloorArea += res.floorAreaSqM
            totalWallArea += res.wallAreaSqM
            totalFloorBoxes += res.floorBoxesToBuy
            totalFloorTilesNeeded += res.totalFloorTilesNeeded
            totalFloorTilesInBoxes += res.totalFloorTilesInBoxes

            if (res.hasSeparateWallTile) {
                hasSeparateWallTiles = true
                totalWallBoxes += res.wallBoxesToBuy
                totalWallTilesNeeded += res.wallTilesNeeded
                totalWallTilesInBoxes += res.totalWallTilesInBoxes
            }

            if (res.skirtingEnabled && res.skirtingMethod == SkirtingMethod.READY_MADE) {
                totalReadyMadeBoxes += res.readyMadeSkirtingBoxes
                totalReadyMadePieces += res.readyMadeSkirtingPieces
            }

            totalSpacers += res.spacersCount

            if (res.edgeStripEnabled && res.edgeStripLengthMeters > 0) {
                if (res.edgeStripMaterial == EdgeStripMaterial.PLASTIC) {
                    plasticLength += res.edgeStripLengthMeters
                    plasticThickness = res.edgeStripThicknessMm
                } else {
                    metalLength += res.edgeStripLengthMeters
                    metalThickness = res.edgeStripThicknessMm
                }
            }
        }

        val grandTotalArea = totalFloorArea + totalWallArea
        val totalFloorSpare = totalFloorTilesInBoxes - totalFloorTilesNeeded
        val totalWallSpare = totalWallTilesInBoxes - totalWallTilesNeeded

        // Adhesive calculations
        val adhConfig = job.adhesiveConfig
        val printedFloorCov = when (adhConfig.brand) {
            AdhesiveBrand.EASY_GRIP -> AdhesiveBrand.EASY_GRIP.defaultFloorCov
            AdhesiveBrand.TILEMATE -> AdhesiveBrand.TILEMATE.defaultFloorCov
            AdhesiveBrand.OTHER -> adhConfig.otherFloorCoverage
        }
        val printedWallCov = when (adhConfig.brand) {
            AdhesiveBrand.EASY_GRIP -> AdhesiveBrand.EASY_GRIP.defaultWallCov
            AdhesiveBrand.TILEMATE -> AdhesiveBrand.TILEMATE.defaultWallCov
            AdhesiveBrand.OTHER -> adhConfig.otherWallCoverage
        }

        val chosenFloorThick = max(5.0, min(50.0, adhConfig.floorThicknessMm))
        val chosenWallThick = max(3.0, min(50.0, adhConfig.wallThicknessMm))

        // Coverage per bag = printed coverage x reference thickness / chosen thickness
        val floorCovPerBag = printedFloorCov * (5.0 / chosenFloorThick)
        val wallCovPerBag = printedWallCov * (3.0 / chosenWallThick)

        val floorBagsRaw = if (floorCovPerBag > 0) totalFloorArea / floorCovPerBag else 0.0
        val wallBagsRaw = if (wallCovPerBag > 0) totalWallArea / wallCovPerBag else 0.0
        val adhesiveBags = ceil(floorBagsRaw + wallBagsRaw).toInt()

        val isBedThickWarning = chosenFloorThick > 10.0

        val bondingLitres = if (adhConfig.includeBondingLiquid) adhesiveBags * 5 else 0

        // Grout
        val groutCov = if (adhConfig.groutCoveragePerBag > 0) adhConfig.groutCoveragePerBag else 10.0
        val groutBags = ceil(grandTotalArea / groutCov).toInt()

        // Edge strip pieces: ceil(total length for that material x 1.10 / piece length)
        val plasticPieces = if (plasticLength > 0) ceil((plasticLength * 1.10) / 2.5).toInt() else 0
        val metalPieces = if (metalLength > 0) ceil((metalLength * 1.10) / 2.5).toInt() else 0

        return JobCalculatedResult(
            totalFloorAreaSqM = totalFloorArea,
            totalWallAreaSqM = totalWallArea,
            grandTotalAreaSqM = grandTotalArea,
            isTileChosen = job.isTileChosen,
            totalFloorBoxes = totalFloorBoxes,
            totalFloorTilesInBoxes = totalFloorTilesInBoxes,
            totalFloorTilesNeeded = totalFloorTilesNeeded,
            totalFloorSpareTiles = totalFloorSpare,
            floorTileSizeLabel = job.sharedFloorTileSpec.sizeLabel,
            floorTilesPerBox = job.sharedFloorTileSpec.tilesPerBox,
            hasSeparateWallTiles = hasSeparateWallTiles,
            totalWallBoxes = totalWallBoxes,
            totalWallTilesInBoxes = totalWallTilesInBoxes,
            totalWallTilesNeeded = totalWallTilesNeeded,
            totalWallSpareTiles = totalWallSpare,
            wallTileSizeLabel = job.sharedWallTileSpec.sizeLabel,
            wallTilesPerBox = job.sharedWallTileSpec.tilesPerBox,
            totalReadyMadeSkirtingBoxes = totalReadyMadeBoxes,
            totalReadyMadeSkirtingPieces = totalReadyMadePieces,
            adhesiveBags = adhesiveBags,
            adhesiveBrandName = adhConfig.brand.displayName,
            floorThicknessMm = chosenFloorThick,
            wallThicknessMm = chosenWallThick,
            floorCoveragePerBag = floorCovPerBag,
            wallCoveragePerBag = wallCovPerBag,
            isBedThickWarning = isBedThickWarning,
            bondingLiquidLitres = bondingLitres,
            groutBags = groutBags,
            totalSpacersCount = totalSpacers,
            spacerSizeMm = job.sharedFloorTileSpec.spacerMm,
            plasticEdgeStripLengthM = plasticLength,
            plasticEdgeStripPieces = plasticPieces,
            plasticTileThicknessMm = plasticThickness,
            metalEdgeStripLengthM = metalLength,
            metalEdgeStripPieces = metalPieces,
            metalTileThicknessMm = metalThickness,
            areaResults = areaResults
        )
    }

    fun generateQuotation(
        job: Job,
        profile: UserProfile,
        detailed: Boolean
    ): String {
        val calc = calculateJob(job)
        val sb = StringBuilder()

        sb.append("TILING QUOTATION\n")
        if (profile.businessName.isNotBlank() || profile.phoneNumber.isNotBlank()) {
            val by = listOf(profile.businessName, profile.phoneNumber).filter { it.isNotBlank() }.joinToString(", ")
            sb.append("Prepared by: $by\n")
        }
        if (job.dateString.isNotBlank()) {
            sb.append("Date: ${job.dateString}\n")
        }
        if (job.clientName.isNotBlank()) {
            sb.append("Client: ${job.clientName}\n")
        }
        val jobAndSite = listOf(job.jobName.ifBlank { "Tiling Project" }, job.siteAddress).filter { it.isNotBlank() }.joinToString(", ")
        sb.append("Job: $jobAndSite\n\n")

        if (detailed) {
            // Detailed breakdown of each area
            job.areas.forEachIndexed { index, area ->
                val res = calc.areaResults.getOrNull(index)
                sb.append("AREA ${index + 1}: ${area.name.uppercase(Locale.getDefault())} (${area.type.title})\n")

                when (area.type) {
                    AreaType.ROOM_FLOOR -> {
                        sb.append("Size: ${area.lengthInput} m x ${area.widthInput} m = ${String.format(Locale.getDefault(), "%.2f", res?.floorAreaSqM ?: 0.0)} m2\n")
                        if (area.doorwayCount > 0) {
                            sb.append("Doorways: ${area.doorwayCount} (${area.doorwayWidthInput} m wide)\n")
                        }
                    }
                    AreaType.BATHROOM -> {
                        sb.append("Floor size: ${area.lengthInput} m x ${area.widthInput} m = ${String.format(Locale.getDefault(), "%.2f", res?.floorAreaSqM ?: 0.0)} m2\n")
                        if (area.bathroomWall.enabled) {
                            sb.append("Wall tiles: ${area.bathroomWall.wallHeightM} m high (walls tiled area: ${String.format(Locale.getDefault(), "%.2f", res?.wallAreaSqM ?: 0.0)} m2)\n")
                        }
                        if (area.bathTub.enabled) {
                            sb.append("Bath tub: built-in tiled tub (${area.bathTub.lengthM} m x ${area.bathTub.widthM} m x ${area.bathTub.heightM} m)\n")
                        }
                    }
                    AreaType.VERANDA -> {
                        sb.append("Veranda top: ${area.lengthInput} m x ${area.widthInput} m = ${String.format(Locale.getDefault(), "%.2f", res?.floorAreaSqM ?: 0.0)} m2\n")
                        if (area.verandaRaised) {
                            sb.append("Raised edges: ${area.verandaRaisedHeightInput} m high (side area: ${String.format(Locale.getDefault(), "%.2f", res?.wallAreaSqM ?: 0.0)} m2)\n")
                        }
                    }
                    AreaType.WALL_ONLY -> {
                        sb.append("Wall size: ${area.wallOnlyLengthInput} m x ${area.wallOnlyHeightInput} m = ${String.format(Locale.getDefault(), "%.2f", res?.wallAreaSqM ?: 0.0)} m2\n")
                    }
                }

                if (res != null) {
                    if (res.isTileChosen) {
                        sb.append("Tile: ${job.sharedFloorTileSpec.sizeLabel}, ${job.sharedFloorTileSpec.tilesPerBox} tiles per box, spacers ${res.spacerSizeMm.toInt()} mm\n")
                        if (res.skirtingTilesAddedToFloor > 0) {
                            sb.append("Tiles needed: ${res.totalFloorTilesNeeded} (${res.floorTilesOnly} for the floor + ${res.skirtingTilesAddedToFloor} cut for the skirting, includes ${job.sharedFloorTileSpec.wastePercent.toInt()}% extra for cuts)\n")
                        } else {
                            sb.append("Tiles needed: ${res.totalFloorTilesNeeded} (includes ${job.sharedFloorTileSpec.wastePercent.toInt()}% extra for cuts)\n")
                        }
                    } else {
                        sb.append("Tile: To be chosen at the shop\n")
                    }

                    if (res.skirtingEnabled && res.skirtingLengthMeters > 0) {
                        val methodDesc = if (res.skirtingMethod == SkirtingMethod.CUT_FROM_FLOOR) "cut from the floor tiles" else "ready-made skirting tiles"
                        sb.append("Skirting: ${String.format(Locale.getDefault(), "%.1f", res.skirtingLengthMeters)} m long, ${res.skirtingHeightCm.toInt()} cm high, $methodDesc\n")
                    }

                    if (res.edgeStripEnabled && res.edgeStripLengthMeters > 0) {
                        val matName = res.edgeStripMaterial.label.lowercase(Locale.getDefault())
                        sb.append("Edge strips: ${String.format(Locale.getDefault(), "%.1f", res.edgeStripLengthMeters)} m of $matName strip\n")
                    }
                }

                if (area.note.isNotBlank()) {
                    sb.append("Note: ${area.note}\n")
                }
                sb.append("\n")
            }
        }

        // MATERIALS TO BUY Section
        sb.append("MATERIALS TO BUY\n")
        if (calc.isTileChosen) {
            sb.append("Tiles: ${calc.totalFloorBoxes} boxes (${calc.totalFloorTilesInBoxes} tiles, ${calc.totalFloorSpareTiles} spare)\n")
            if (calc.hasSeparateWallTiles) {
                sb.append("Wall tiles: ${calc.totalWallBoxes} boxes (${calc.totalWallTilesInBoxes} tiles, ${calc.totalWallSpareTiles} spare)\n")
            }
        } else {
            sb.append("Tiles: Area measured (${String.format(Locale.getDefault(), "%.2f", calc.grandTotalAreaSqM)} m2 total). Choose tile at shop for box count.\n")
        }

        if (calc.totalReadyMadeSkirtingBoxes > 0) {
            sb.append("Skirting tiles: ${calc.totalReadyMadeSkirtingBoxes} boxes (${calc.totalReadyMadeSkirtingPieces} pieces)\n")
        }

        sb.append("Tile adhesive (${calc.adhesiveBrandName}): ${calc.adhesiveBags} bags, ${calc.floorThicknessMm.toInt()} mm thick\n")
        sb.append("Grout: ${calc.groutBags} bags\n")
        if (calc.bondingLiquidLitres > 0) {
            sb.append("Bonding liquid (optional): ${calc.bondingLiquidLitres} litres\n")
        }
        if (calc.isTileChosen && calc.totalSpacersCount > 0) {
            sb.append("Tile spacers (${calc.spacerSizeMm.toInt()} mm): ${calc.totalSpacersCount} pieces\n")
        }
        if (calc.plasticEdgeStripPieces > 0) {
            sb.append("Plastic edge strips (${calc.plasticTileThicknessMm} mm, 2.5 m long): ${calc.plasticEdgeStripPieces} pieces\n")
        }
        if (calc.metalEdgeStripPieces > 0) {
            sb.append("Metal edge strips (${calc.metalTileThicknessMm} mm, 2.5 m long): ${calc.metalEdgeStripPieces} pieces\n")
        }

        sb.append("\nNOTES\n")
        if (job.notes.isNotBlank()) {
            sb.append("${job.notes}\n")
        }
        sb.append("Estimates only. Quantities depend on site conditions. Check the coverage printed on the bags.\n")

        return sb.toString()
    }
}
