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
    val skirtingAreaSqM: Double,
    val totalAreaSqM: Double,

    // Floor Tiles (when tile chosen)
    val isTileChosen: Boolean,
    val floorTilesOnly: Int,
    val skirtingTilesAddedToFloor: Int,
    val totalFloorTilesNeeded: Int,
    val floorBoxesToBuy: Int,
    val totalFloorTilesInBoxes: Int,
    val floorSpareTiles: Int,
    val floorTileSizeLabel: String = "60 x 60 cm",
    val floorTilesPerBox: Int = 12,

    // Separate wall tiles
    val hasWallTiles: Boolean = false,
    val wallTilesNeeded: Int = 0,
    val wallBoxesToBuy: Int = 0,
    val totalWallTilesInBoxes: Int = 0,
    val wallSpareTiles: Int = 0,
    val wallTileSizeLabel: String = "30 x 60 cm",
    val wallTilesPerBox: Int = 8,

    // Skirting
    val skirtingEnabled: Boolean,
    val skirtingLengthMeters: Double,
    val skirtingHeightCm: Double,
    val skirtingMethod: SkirtingMethod,
    val readyMadeSkirtingPieces: Int,
    val readyMadeSkirtingBoxes: Int,
    val skirtingBedThicknessMm: Double,

    // Grout breakdown for this area (kg & bags)
    val floorGroutKg: Double,
    val wallGroutKg: Double,
    val skirtingGroutKg: Double,
    val totalGroutKg: Double,
    val floorGroutBags: Int = 0,
    val wallGroutBags: Int = 0,

    // Adhesive bags raw & integer bags
    val floorAdhesiveBagsRaw: Double,
    val wallAdhesiveBagsRaw: Double,
    val skirtingAdhesiveBagsRaw: Double,
    val floorAdhesiveBags: Int = 0,
    val wallAdhesiveBags: Int = 0,

    // Edge strips for this area
    val edgeStripEnabled: Boolean,
    val edgeStripMaterial: EdgeStripMaterial,
    val edgeStripLengthMeters: Double,
    val edgeStripPieces: Int,
    val edgeStripThicknessMm: Int,
    val edgeStripPieceLengthM: Double,

    // Spacers for this area (separate floor and wall)
    val floorSpacersCount: Int = 0,
    val wallSpacersCount: Int = 0,
    val spacersCount: Int = 0,
    val floorSpacerMm: Double = 3.0,
    val wallSpacerMm: Double = 2.0,
    val spacerSizeMm: Double = 3.0
) {
    // Backwards compatibility property
    val hasSeparateWallTile: Boolean get() = hasWallTiles
}

data class JobCalculatedResult(
    val totalFloorAreaSqM: Double,
    val totalWallAreaSqM: Double,
    val totalSkirtingAreaSqM: Double,
    val grandTotalAreaSqM: Double,

    // Floor Tiles
    val isTileChosen: Boolean,
    val totalFloorBoxes: Int,
    val totalFloorTilesInBoxes: Int,
    val totalFloorTilesNeeded: Int,
    val totalFloorSpareTiles: Int,
    val floorTileSizeLabel: String,
    val floorTilesPerBox: Int,

    // Wall tiles (calculated separately from floor tiles!)
    val hasWallTiles: Boolean,
    val totalWallBoxes: Int,
    val totalWallTilesInBoxes: Int,
    val totalWallTilesNeeded: Int,
    val totalWallSpareTiles: Int,
    val wallTileSizeLabel: String,
    val wallTilesPerBox: Int,

    // Ready-made skirting
    val totalReadyMadeSkirtingBoxes: Int,
    val totalReadyMadeSkirtingPieces: Int,

    // Adhesive (calculated separately for floor, wall, skirting)
    val adhesiveBags: Int, // grand total
    val floorAdhesiveBags: Int,
    val wallAdhesiveBags: Int,
    val skirtingAdhesiveBags: Int,
    val adhesiveBrandName: String,
    val floorThicknessMm: Double,
    val wallThicknessMm: Double,
    val skirtingThicknessMm: Double,
    val floorCoveragePerBag: Double,
    val wallCoveragePerBag: Double,
    val skirtingCoveragePerBag: Double,
    val floorBagsRaw: Double,
    val wallBagsRaw: Double,
    val skirtingBagsRaw: Double,
    val isBedThickWarning: Boolean, // > 10 mm on floor
    val bondingLiquidLitres: Double,
    val bondingLiquidMode: BondingLiquidMode,
    val bondingLiquidMlPerBag: Double,
    val bondingLiquidResultText: String,

    // Grout (calculated separately for floor and walls)
    val groutBags: Int, // grand total
    val floorGroutBags: Int,
    val wallGroutBags: Int,
    val floorGroutKg: Double,
    val wallGroutKg: Double,
    val totalGroutKg: Double,
    val groutDensity: Double,
    val groutBagSizeKg: Int,
    val jointDepthMm: Double,
    val floorJointDepthMm: Double = 10.0,
    val wallJointDepthMm: Double = 8.0,

    // Spacers (calculated separately for floor and walls)
    val totalSpacersCount: Int,
    val floorSpacersCount: Int,
    val wallSpacersCount: Int,
    val spacerSizeMm: Double,
    val floorSpacerMm: Double = 3.0,
    val wallSpacerMm: Double = 2.0,

    // Edge strips
    val plasticEdgeStripLengthM: Double,
    val plasticEdgeStripPieces: Int,
    val plasticTileThicknessMm: Int,

    val metalEdgeStripLengthM: Double,
    val metalEdgeStripPieces: Int,
    val metalTileThicknessMm: Int,

    // Area results breakdown
    val areaResults: List<AreaCalculatedResult>
) {
    // Backwards compatibility property
    val hasSeparateWallTiles: Boolean get() = hasWallTiles
}

object TileCalculatorEngine {

    fun formatLitres(litres: Double): String {
        val rounded = kotlin.math.round(litres * 100.0) / 100.0
        val numStr = if (rounded % 1.0 == 0.0) {
            rounded.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", rounded).trimEnd('0').trimEnd('.')
        }
        return if (rounded == 1.0) "$numStr litre" else "$numStr litres"
    }

    fun formatBondingLiquidResult(
        include: Boolean,
        mode: BondingLiquidMode,
        litres: Double,
        mlPerBag: Double
    ): String {
        if (!include || litres <= 0.0) return ""
        val litresStr = formatLitres(litres)
        return when (mode) {
            BondingLiquidMode.ADDITIVE_SPLASH -> {
                val mlText = if (mlPerBag % 1.0 == 0.0) "${mlPerBag.toInt()} ml" else "$mlPerBag ml"
                "Bonding liquid: $litresStr (about $mlText per bag of adhesive)"
            }
            BondingLiquidMode.WET_AREA_FULL -> {
                "Bonding liquid: $litresStr (wet area: replaces all mixing water)"
            }
        }
    }

    fun calculateSurfaceGroutKg(
        tileLengthMm: Double,
        tileWidthMm: Double,
        jointWidthMm: Double,
        jointDepthMm: Double,
        groutDensity: Double,
        tiledAreaSqM: Double
    ): Double {
        if (tileLengthMm <= 0.0 || tileWidthMm <= 0.0 || jointWidthMm <= 0.0 || jointDepthMm <= 0.0 || tiledAreaSqM <= 0.0) {
            return 0.0
        }
        val factor = (tileLengthMm + tileWidthMm) / (tileLengthMm * tileWidthMm)
        return factor * jointWidthMm * jointDepthMm * groutDensity * tiledAreaSqM
    }

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
                    val doorOpenings = area.bathroomWall.doorCount * area.bathroomWall.doorWidthM * area.bathroomWall.doorHeightM
                    val windowOpenings = area.bathroomWall.windowCount * area.bathroomWall.windowWidthM * area.bathroomWall.windowHeightM
                    val grossWallArea = if (area.bathroomWall.entryMode == BathroomWallEntryMode.PER_WALL) {
                        var g = 0.0
                        area.bathroomWall.walls.forEach { w ->
                            val l = unit.toMeters(parseNum(w.lengthInput))
                            val h = unit.toMeters(parseNum(w.heightInput))
                            g += (l * h)
                        }
                        if (g <= 0.0 && lengthM > 0.0) {
                            val wHeight = if (area.bathroomWall.wallHeightM > 0) area.bathroomWall.wallHeightM else 2.4
                            2.0 * (lengthM + widthM) * wHeight
                        } else g
                    } else {
                        val inputArea = parseNum(area.bathroomWall.totalWallAreaInput)
                        if (inputArea > 0.0) inputArea else {
                            val wHeight = if (area.bathroomWall.wallHeightM > 0) area.bathroomWall.wallHeightM else 2.4
                            2.0 * (lengthM + widthM) * wHeight
                        }
                    }
                    wallArea += max(0.0, grossWallArea - (doorOpenings + windowOpenings))
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
                    val perimeterForTopEdge = if (area.bathroomWall.entryMode == BathroomWallEntryMode.PER_WALL && area.bathroomWall.walls.isNotEmpty()) {
                        area.bathroomWall.walls.sumOf { unit.toMeters(parseNum(it.lengthInput)) }
                    } else {
                        2.0 * (lengthM + widthM)
                    }
                    bathEdge += max(0.0, perimeterForTopEdge - (area.bathroomWall.doorCount * area.bathroomWall.doorWidthM))
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

        // Skirting wall area (Skirting counts as its own surface)
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
        val isBathroom = area.type == AreaType.BATHROOM
        val floorTileSpec = if (isBathroom) {
            area.bathroomFloorTileSpec
        } else if (job.sameTileForAllAreas) {
            job.sharedFloorTileSpec
        } else {
            (area.customTileSpec ?: job.sharedFloorTileSpec)
        }

        val useSeparateWallTile = (isBathroom || !job.sameTileForWallsAndSides) && wallArea > 0
        val wallTileSpec = if (isBathroom) {
            area.bathroomWallTileSpec
        } else if (useSeparateWallTile) {
            job.sharedWallTileSpec
        } else {
            floorTileSpec
        }

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
        var floorSpacersCount = 0
        var wallSpacersCount = 0

        val tileChosen = job.isTileChosen || isBathroom

        if (tileChosen) {
            val spacerM = floorTileSpec.spacerMm / 1000.0
            val effFloorTileL = (floorTileSpec.lengthCm / 100.0) + spacerM
            val effFloorTileW = (floorTileSpec.widthCm / 100.0) + spacerM
            val effFloorTileArea = max(0.0001, effFloorTileL * effFloorTileW)

            // Floor surface tiles
            if (floorArea > 0) {
                val rawFloorTiles = floorArea / effFloorTileArea
                floorTilesOnly = ceil(rawFloorTiles * (1.0 + floorTileSpec.wastePercent / 100.0)).toInt()
            }

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

            // If walls use the SAME tile and not separate (only for non-bathroom areas)
            val sameTileWallTiles = if (!isBathroom && !useSeparateWallTile && wallArea > 0) {
                val rawWallTiles = wallArea / effFloorTileArea
                ceil(rawWallTiles * (1.0 + floorTileSpec.wastePercent / 100.0)).toInt()
            } else 0

            totalFloorTilesNeeded = floorTilesOnly + skirtingTilesAddedToFloor + sameTileWallTiles
            val boxCapacity = max(1, floorTileSpec.tilesPerBox)
            floorBoxesToBuy = if (totalFloorTilesNeeded > 0) ceil(totalFloorTilesNeeded.toDouble() / boxCapacity).toInt() else 0
            totalFloorTilesInBoxes = floorBoxesToBuy * boxCapacity
            floorSpareTiles = totalFloorTilesInBoxes - totalFloorTilesNeeded

            // Separate wall tile calculation
            if (useSeparateWallTile && wallArea > 0) {
                val wallSpacerM = wallTileSpec.spacerMm / 1000.0
                val effWallTileL = (wallTileSpec.lengthCm / 100.0) + wallSpacerM
                val effWallTileW = (wallTileSpec.widthCm / 100.0) + wallSpacerM
                val effWallTileArea = max(0.0001, effWallTileL * effWallTileW)
                val rawWallTiles = wallArea / effWallTileArea
                wallTilesNeeded = ceil(rawWallTiles * (1.0 + wallTileSpec.wastePercent / 100.0)).toInt()
                val wallBoxCapacity = max(1, wallTileSpec.tilesPerBox)
                wallBoxesToBuy = if (wallTilesNeeded > 0) ceil(wallTilesNeeded.toDouble() / wallBoxCapacity).toInt() else 0
                totalWallTilesInBoxes = wallBoxesToBuy * wallBoxCapacity
                wallSpareTiles = totalWallTilesInBoxes - wallTilesNeeded
            }

            // Spacers for floor
            if (floorArea > 0 && lengthM > 0 && widthM > 0) {
                val tilesAlong = ceil(lengthM / effFloorTileL)
                val tilesAcross = ceil(widthM / effFloorTileW)
                floorSpacersCount = ceil((tilesAlong + 1) * (tilesAcross + 1) * 1.10).toInt()
            }
            // Spacers for wall
            if (wallArea > 0) {
                val effWallArea = if (useSeparateWallTile) {
                    val wSpacerM = wallTileSpec.spacerMm / 1000.0
                    ((wallTileSpec.lengthCm / 100.0) + wSpacerM) * ((wallTileSpec.widthCm / 100.0) + wSpacerM)
                } else effFloorTileArea
                val tilesBeforeWaste = wallArea / max(0.0001, effWallArea)
                wallSpacersCount = ceil((tilesBeforeWaste + 2.0 * sqrt(tilesBeforeWaste) + 1.0) * 1.10).toInt()
            }
        }

        val totalAreaSpacers = floorSpacersCount + wallSpacersCount

        // Edge strip pieces for this area
        val stripPieceLen = if (area.edgeStrip.pieceLengthMeters > 0) area.edgeStrip.pieceLengthMeters else 2.5
        val areaEdgePieces = if (actualEdgeStripLengthM > 0) {
            ceil((actualEdgeStripLengthM * 1.10) / stripPieceLen).toInt()
        } else 0

        // Adhesive calculation for this area:
        // Floor uses floor reference coverage at 5 mm and floor bed thickness
        // Wall uses wall reference coverage at 3 mm and wall bed thickness
        // Skirting uses wall reference coverage at 3 mm and its OWN skirting bed thickness
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

        val chosenFloorThick = max(3.0, min(50.0, adhConfig.floorThicknessMm))
        val wallThick = if (adhConfig.customWallThicknessMm.isNotBlank()) {
            parseNum(adhConfig.customWallThicknessMm).let { if (it > 0) it else adhConfig.wallThicknessMm }
        } else adhConfig.wallThicknessMm
        val chosenWallThick = max(1.0, min(50.0, wallThick))

        val skThickVal = if (area.skirting.customBedThicknessMm.isNotBlank()) {
            parseNum(area.skirting.customBedThicknessMm).let { if (it > 0) it else area.skirting.bedThicknessMm }
        } else area.skirting.bedThicknessMm
        val chosenSkirtingThick = max(1.0, min(50.0, if (skThickVal > 0) skThickVal else adhConfig.skirtingThicknessMm))

        val floorCovPerBag = printedFloorCov * (5.0 / chosenFloorThick)
        val wallCovPerBag = printedWallCov * (3.0 / chosenWallThick)
        val skirtingCovPerBag = printedWallCov * (3.0 / chosenSkirtingThick)

        val areaFloorBagsRaw = if (floorCovPerBag > 0) floorArea / floorCovPerBag else 0.0
        val areaWallBagsRaw = if (wallCovPerBag > 0) wallArea / wallCovPerBag else 0.0
        val areaSkirtingBagsRaw = if (skirtingActive && skirtingCovPerBag > 0) skirtingWallAreaSqM / skirtingCovPerBag else 0.0
        val floorAdhBags = ceil(areaFloorBagsRaw).toInt()
        val wallAdhBags = ceil(areaWallBagsRaw).toInt()

        // Grout geometric calculation for each surface in this area:
        val groutDensity = if (adhConfig.groutDensity > 0) adhConfig.groutDensity else 1.6
        val floorTileLenMm = floorTileSpec.lengthCm * 10.0
        val floorTileWidthMm = floorTileSpec.widthCm * 10.0
        val floorJointWidthMm = max(1.0, floorTileSpec.spacerMm)
        val floorJointDepthMm = max(1.0, floorTileSpec.jointDepthMm)

        val floorGroutKg = calculateSurfaceGroutKg(
            tileLengthMm = floorTileLenMm,
            tileWidthMm = floorTileWidthMm,
            jointWidthMm = floorJointWidthMm,
            jointDepthMm = floorJointDepthMm,
            groutDensity = groutDensity,
            tiledAreaSqM = floorArea
        )

        val wallTileLenMm = wallTileSpec.lengthCm * 10.0
        val wallTileWidthMm = wallTileSpec.widthCm * 10.0
        val wallJointWidthMm = max(1.0, wallTileSpec.spacerMm)
        val wallJointDepthMm = max(1.0, wallTileSpec.jointDepthMm)

        val wallGroutKg = calculateSurfaceGroutKg(
            tileLengthMm = wallTileLenMm,
            tileWidthMm = wallTileWidthMm,
            jointWidthMm = wallJointWidthMm,
            jointDepthMm = wallJointDepthMm,
            groutDensity = groutDensity,
            tiledAreaSqM = wallArea
        )

        val skirtingTileLenMm = if (area.skirting.method == SkirtingMethod.CUT_FROM_FLOOR) {
            floorTileLenMm
        } else {
            max(10.0, area.skirting.readyMadePieceLenCm * 10.0)
        }
        val skirtingTileWidthMm = max(10.0, skirtingHeightCm * 10.0)

        val skirtingGroutKg = if (skirtingActive && skirtingWallAreaSqM > 0) {
            calculateSurfaceGroutKg(
                tileLengthMm = skirtingTileLenMm,
                tileWidthMm = skirtingTileWidthMm,
                jointWidthMm = floorJointWidthMm,
                jointDepthMm = floorJointDepthMm,
                groutDensity = groutDensity,
                tiledAreaSqM = skirtingWallAreaSqM
            )
        } else 0.0

        val areaTotalGroutKg = floorGroutKg + wallGroutKg + skirtingGroutKg
        val groutBagSize = if (adhConfig.groutBagSizeKg > 0) adhConfig.groutBagSizeKg else 5
        val floorGroutBags = ceil(floorGroutKg / groutBagSize.toDouble()).toInt().coerceAtLeast(if (floorGroutKg > 0) 1 else 0)
        val wallGroutBags = ceil(wallGroutKg / groutBagSize.toDouble()).toInt().coerceAtLeast(if (wallGroutKg > 0) 1 else 0)

        return AreaCalculatedResult(
            areaId = area.id,
            areaName = area.name.ifBlank { area.type.title },
            areaType = area.type,
            floorAreaSqM = floorArea,
            wallAreaSqM = wallArea,
            skirtingAreaSqM = skirtingWallAreaSqM,
            totalAreaSqM = totalAreaWithSkirting,
            isTileChosen = tileChosen,
            floorTilesOnly = floorTilesOnly,
            skirtingTilesAddedToFloor = skirtingTilesAddedToFloor,
            totalFloorTilesNeeded = totalFloorTilesNeeded,
            floorBoxesToBuy = floorBoxesToBuy,
            totalFloorTilesInBoxes = totalFloorTilesInBoxes,
            floorSpareTiles = floorSpareTiles,
            floorTileSizeLabel = floorTileSpec.sizeLabel,
            floorTilesPerBox = floorTileSpec.tilesPerBox,
            hasWallTiles = (useSeparateWallTile || isBathroom) && wallArea > 0,
            wallTilesNeeded = wallTilesNeeded,
            wallBoxesToBuy = wallBoxesToBuy,
            totalWallTilesInBoxes = totalWallTilesInBoxes,
            wallSpareTiles = wallSpareTiles,
            wallTileSizeLabel = wallTileSpec.sizeLabel,
            wallTilesPerBox = wallTileSpec.tilesPerBox,
            skirtingEnabled = skirtingActive,
            skirtingLengthMeters = actualSkirtingLengthM,
            skirtingHeightCm = skirtingHeightCm,
            skirtingMethod = area.skirting.method,
            readyMadeSkirtingPieces = readyMadePieces,
            readyMadeSkirtingBoxes = readyMadeBoxes,
            skirtingBedThicknessMm = chosenSkirtingThick,
            floorGroutKg = floorGroutKg,
            wallGroutKg = wallGroutKg,
            skirtingGroutKg = skirtingGroutKg,
            totalGroutKg = areaTotalGroutKg,
            floorGroutBags = floorGroutBags,
            wallGroutBags = wallGroutBags,
            floorAdhesiveBagsRaw = areaFloorBagsRaw,
            wallAdhesiveBagsRaw = areaWallBagsRaw,
            skirtingAdhesiveBagsRaw = areaSkirtingBagsRaw,
            floorAdhesiveBags = floorAdhBags,
            wallAdhesiveBags = wallAdhBags,
            edgeStripEnabled = area.edgeStrip.enabled,
            edgeStripMaterial = area.edgeStrip.material,
            edgeStripLengthMeters = actualEdgeStripLengthM,
            edgeStripPieces = areaEdgePieces,
            edgeStripThicknessMm = area.edgeStrip.tileThicknessMm,
            edgeStripPieceLengthM = stripPieceLen,
            floorSpacersCount = floorSpacersCount,
            wallSpacersCount = wallSpacersCount,
            spacersCount = totalAreaSpacers,
            floorSpacerMm = floorTileSpec.spacerMm,
            wallSpacerMm = wallTileSpec.spacerMm,
            spacerSizeMm = floorTileSpec.spacerMm
        )
    }

    fun calculateJob(job: Job): JobCalculatedResult {
        val areaResults = job.areas.map { calculateArea(it, job) }

        var totalFloorArea = 0.0
        var totalWallArea = 0.0
        var totalSkirtingArea = 0.0
        var totalFloorBoxes = 0
        var totalFloorTilesNeeded = 0
        var totalFloorTilesInBoxes = 0

        var hasWallTiles = false
        var totalWallBoxes = 0
        var totalWallTilesNeeded = 0
        var totalWallTilesInBoxes = 0

        var totalReadyMadeBoxes = 0
        var totalReadyMadePieces = 0

        var totalSpacers = 0
        var totalFloorSpacers = 0
        var totalWallSpacers = 0

        var plasticLength = 0.0
        var metalLength = 0.0
        var plasticThickness = 10
        var metalThickness = 10

        var totalFloorBagsRaw = 0.0
        var totalWallBagsRaw = 0.0
        var totalSkirtingBagsRaw = 0.0
        var totalFloorGroutKg = 0.0
        var totalWallGroutKg = 0.0
        var totalGroutKg = 0.0

        areaResults.forEach { res ->
            totalFloorArea += res.floorAreaSqM
            totalWallArea += res.wallAreaSqM
            totalSkirtingArea += res.skirtingAreaSqM
            totalFloorBoxes += res.floorBoxesToBuy
            totalFloorTilesNeeded += res.totalFloorTilesNeeded
            totalFloorTilesInBoxes += res.totalFloorTilesInBoxes

            if (res.hasWallTiles) {
                hasWallTiles = true
                totalWallBoxes += res.wallBoxesToBuy
                totalWallTilesNeeded += res.wallTilesNeeded
                totalWallTilesInBoxes += res.totalWallTilesInBoxes
            }

            if (res.skirtingEnabled && res.skirtingMethod == SkirtingMethod.READY_MADE) {
                totalReadyMadeBoxes += res.readyMadeSkirtingBoxes
                totalReadyMadePieces += res.readyMadeSkirtingPieces
            }

            totalSpacers += res.spacersCount
            totalFloorSpacers += res.floorSpacersCount
            totalWallSpacers += res.wallSpacersCount

            totalFloorBagsRaw += res.floorAdhesiveBagsRaw
            totalWallBagsRaw += res.wallAdhesiveBagsRaw
            totalSkirtingBagsRaw += res.skirtingAdhesiveBagsRaw

            totalFloorGroutKg += res.floorGroutKg
            totalWallGroutKg += res.wallGroutKg
            totalGroutKg += res.totalGroutKg

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

        val grandTotalArea = totalFloorArea + totalWallArea + totalSkirtingArea
        val totalFloorSpare = totalFloorTilesInBoxes - totalFloorTilesNeeded
        val totalWallSpare = totalWallTilesInBoxes - totalWallTilesNeeded

        // Adhesive calculations:
        // Floor uses floor reference (e.g. 3.5 m2/bag at 5 mm)
        // Walls & skirting use wall reference (e.g. 5.0 m2/bag at 3 mm)
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

        val chosenFloorThick = max(3.0, min(50.0, adhConfig.floorThicknessMm))
        val wallThick = if (adhConfig.customWallThicknessMm.isNotBlank()) {
            parseNum(adhConfig.customWallThicknessMm).let { if (it > 0) it else adhConfig.wallThicknessMm }
        } else adhConfig.wallThicknessMm
        val chosenWallThick = max(1.0, min(50.0, wallThick))

        val skirtingThick = if (adhConfig.customSkirtingThicknessMm.isNotBlank()) {
            parseNum(adhConfig.customSkirtingThicknessMm).let { if (it > 0) it else adhConfig.skirtingThicknessMm }
        } else adhConfig.skirtingThicknessMm
        val chosenSkirtingThick = max(1.0, min(50.0, skirtingThick))

        val floorCovPerBag = printedFloorCov * (5.0 / chosenFloorThick)
        val wallCovPerBag = printedWallCov * (3.0 / chosenWallThick)
        val skirtingCovPerBag = printedWallCov * (3.0 / chosenSkirtingThick)

        val floorAdhesiveBags = ceil(totalFloorBagsRaw).toInt()
        val wallAdhesiveBags = ceil(totalWallBagsRaw).toInt()
        val skirtingAdhesiveBags = ceil(totalSkirtingBagsRaw).toInt()
        val adhesiveBags = ceil(totalFloorBagsRaw + totalWallBagsRaw + totalSkirtingBagsRaw).toInt()
        val isBedThickWarning = chosenFloorThick > 10.0

        // Bonding liquid
        val effectiveMlPerBag = if (adhConfig.customBondingMl.isNotBlank()) {
            val parsed = parseNum(adhConfig.customBondingMl)
            if (parsed > 0) parsed else adhConfig.bondingMlPerBag
        } else {
            adhConfig.bondingMlPerBag
        }

        val bondingLitres: Double = if (!adhConfig.includeBondingLiquid || adhesiveBags <= 0) {
            0.0
        } else {
            when (adhConfig.bondingLiquidMode) {
                BondingLiquidMode.ADDITIVE_SPLASH -> {
                    val rawLitres = adhesiveBags * (effectiveMlPerBag / 1000.0)
                    val rounded = ceil(rawLitres / 0.25) * 0.25
                    kotlin.math.round(rounded * 100.0) / 100.0
                }
                BondingLiquidMode.WET_AREA_FULL -> {
                    adhesiveBags * 5.0
                }
            }
        }

        val bondingResultText = formatBondingLiquidResult(
            include = adhConfig.includeBondingLiquid,
            mode = adhConfig.bondingLiquidMode,
            litres = bondingLitres,
            mlPerBag = effectiveMlPerBag
        )

        // Grout bags rounded UP to bag size (2 kg or 5 kg, default 5 kg)
        val bagSize = if (adhConfig.groutBagSizeKg > 0) adhConfig.groutBagSizeKg else 5
        val floorGroutBags = ceil(totalFloorGroutKg / bagSize.toDouble()).toInt().coerceAtLeast(if (totalFloorGroutKg > 0) 1 else 0)
        val wallGroutBags = ceil(totalWallGroutKg / bagSize.toDouble()).toInt().coerceAtLeast(if (totalWallGroutKg > 0) 1 else 0)
        val groutBags = ceil(totalGroutKg / bagSize.toDouble()).toInt().coerceAtLeast(if (totalGroutKg > 0) 1 else 0)

        // Edge strip pieces: ceil(total length for that material x 1.10 / piece length)
        val plasticPieces = if (plasticLength > 0) ceil((plasticLength * 1.10) / 2.5).toInt() else 0
        val metalPieces = if (metalLength > 0) ceil((metalLength * 1.10) / 2.5).toInt() else 0

        val chosenFloorTileLabel = areaResults.firstOrNull { it.floorBoxesToBuy > 0 }?.floorTileSizeLabel ?: job.sharedFloorTileSpec.sizeLabel
        val chosenFloorTilesBox = areaResults.firstOrNull { it.floorBoxesToBuy > 0 }?.floorTilesPerBox ?: job.sharedFloorTileSpec.tilesPerBox
        val chosenWallTileLabel = areaResults.firstOrNull { it.wallBoxesToBuy > 0 }?.wallTileSizeLabel ?: job.sharedWallTileSpec.sizeLabel
        val chosenWallTilesBox = areaResults.firstOrNull { it.wallBoxesToBuy > 0 }?.wallTilesPerBox ?: job.sharedWallTileSpec.tilesPerBox
        val floorJointDepth = job.sharedFloorTileSpec.jointDepthMm
        val wallJointDepth = 8.0
        val isJobTileChosen = job.isTileChosen || areaResults.any { it.isTileChosen && (it.floorBoxesToBuy > 0 || it.wallBoxesToBuy > 0) }

        return JobCalculatedResult(
            totalFloorAreaSqM = totalFloorArea,
            totalWallAreaSqM = totalWallArea,
            totalSkirtingAreaSqM = totalSkirtingArea,
            grandTotalAreaSqM = grandTotalArea,
            isTileChosen = isJobTileChosen,
            totalFloorBoxes = totalFloorBoxes,
            totalFloorTilesInBoxes = totalFloorTilesInBoxes,
            totalFloorTilesNeeded = totalFloorTilesNeeded,
            totalFloorSpareTiles = totalFloorSpare,
            floorTileSizeLabel = chosenFloorTileLabel,
            floorTilesPerBox = chosenFloorTilesBox,
            hasWallTiles = hasWallTiles,
            totalWallBoxes = totalWallBoxes,
            totalWallTilesInBoxes = totalWallTilesInBoxes,
            totalWallTilesNeeded = totalWallTilesNeeded,
            totalWallSpareTiles = totalWallSpare,
            wallTileSizeLabel = chosenWallTileLabel,
            wallTilesPerBox = chosenWallTilesBox,
            totalReadyMadeSkirtingBoxes = totalReadyMadeBoxes,
            totalReadyMadeSkirtingPieces = totalReadyMadePieces,
            adhesiveBags = adhesiveBags,
            floorAdhesiveBags = floorAdhesiveBags,
            wallAdhesiveBags = wallAdhesiveBags,
            skirtingAdhesiveBags = skirtingAdhesiveBags,
            adhesiveBrandName = adhConfig.brand.displayName,
            floorThicknessMm = chosenFloorThick,
            wallThicknessMm = chosenWallThick,
            skirtingThicknessMm = chosenSkirtingThick,
            floorCoveragePerBag = floorCovPerBag,
            wallCoveragePerBag = wallCovPerBag,
            skirtingCoveragePerBag = skirtingCovPerBag,
            floorBagsRaw = totalFloorBagsRaw,
            wallBagsRaw = totalWallBagsRaw,
            skirtingBagsRaw = totalSkirtingBagsRaw,
            isBedThickWarning = isBedThickWarning,
            bondingLiquidLitres = bondingLitres,
            bondingLiquidMode = adhConfig.bondingLiquidMode,
            bondingLiquidMlPerBag = effectiveMlPerBag,
            bondingLiquidResultText = bondingResultText,
            groutBags = groutBags,
            floorGroutBags = floorGroutBags,
            wallGroutBags = wallGroutBags,
            floorGroutKg = totalFloorGroutKg,
            wallGroutKg = totalWallGroutKg,
            totalGroutKg = totalGroutKg,
            groutDensity = adhConfig.groutDensity,
            groutBagSizeKg = bagSize,
            jointDepthMm = job.sharedFloorTileSpec.jointDepthMm,
            floorJointDepthMm = floorJointDepth,
            wallJointDepthMm = wallJointDepth,
            totalSpacersCount = totalSpacers,
            floorSpacersCount = totalFloorSpacers,
            wallSpacersCount = totalWallSpacers,
            spacerSizeMm = job.sharedFloorTileSpec.spacerMm,
            floorSpacerMm = areaResults.firstOrNull()?.floorSpacerMm ?: job.sharedFloorTileSpec.spacerMm,
            wallSpacerMm = areaResults.firstOrNull { it.hasWallTiles }?.wallSpacerMm ?: 2.0,
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
                        sb.append("Floor: ${area.lengthInput} m x ${area.widthInput} m = ${String.format(Locale.getDefault(), "%.2f", res?.floorAreaSqM ?: 0.0)} m2\n")
                        if (res != null && res.floorBoxesToBuy > 0) {
                            sb.append("Floor tiles needed (${res.floorTileSizeLabel}): ${res.totalFloorTilesNeeded} tiles (${res.floorBoxesToBuy} boxes, ${res.floorSpareTiles} spare)\n")
                        }
                        if (area.bathroomWall.enabled && (res?.wallAreaSqM ?: 0.0) > 0) {
                            sb.append("Walls: ${String.format(Locale.getDefault(), "%.2f", res?.wallAreaSqM ?: 0.0)} m2\n")
                            if (res != null && res.wallBoxesToBuy > 0) {
                                sb.append("Wall tiles needed (${res.wallTileSizeLabel}): ${res.wallTilesNeeded} tiles (${res.wallBoxesToBuy} boxes, ${res.wallSpareTiles} spare)\n")
                            }
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
                    if (res.isTileChosen && area.type != AreaType.BATHROOM) {
                        sb.append("Tile: ${res.floorTileSizeLabel}, ${res.floorTilesPerBox} tiles per box, spacers ${res.spacerSizeMm.toInt()} mm\n")
                        if (res.skirtingTilesAddedToFloor > 0) {
                            sb.append("Tiles needed: ${res.totalFloorTilesNeeded} (${res.floorTilesOnly} for the floor + ${res.skirtingTilesAddedToFloor} cut for the skirting, includes ${job.sharedFloorTileSpec.wastePercent.toInt()}% extra for cuts)\n")
                        } else {
                            sb.append("Tiles needed: ${res.totalFloorTilesNeeded} (includes ${job.sharedFloorTileSpec.wastePercent.toInt()}% extra for cuts)\n")
                        }
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
            if (calc.totalFloorBoxes > 0) {
                sb.append("Floor tiles needed: ${calc.totalFloorBoxes} boxes (${calc.totalFloorTilesInBoxes} tiles, ${calc.totalFloorSpareTiles} spare)\n")
            }
            if (calc.hasWallTiles && calc.totalWallBoxes > 0) {
                sb.append("Wall tiles needed: ${calc.totalWallBoxes} boxes (${calc.totalWallTilesInBoxes} tiles, ${calc.totalWallSpareTiles} spare)\n")
            }
        } else {
            sb.append("Tiles: Area measured (${String.format(Locale.getDefault(), "%.2f", calc.grandTotalAreaSqM)} m2 total). Choose tile at shop for box count.\n")
        }

        if (calc.totalReadyMadeSkirtingBoxes > 0) {
            sb.append("Skirting tiles: ${calc.totalReadyMadeSkirtingBoxes} boxes (${calc.totalReadyMadeSkirtingPieces} pieces)\n")
        }

        if (calc.hasWallTiles && calc.totalFloorAreaSqM > 0) {
            sb.append("Tile adhesive (${calc.adhesiveBrandName}): ${calc.adhesiveBags} bags (Floor: ${calc.floorAdhesiveBags} bags at ${calc.floorThicknessMm.toInt()} mm bed, Walls: ${calc.wallAdhesiveBags} bags at ${calc.wallThicknessMm.toInt()} mm bed)\n")
            sb.append("Tile grout: ${calc.groutBags} ${if (calc.groutBags == 1) "bag" else "bags"} (${calc.groutBagSizeKg} kg each, ${String.format(Locale.US, "%.1f", calc.totalGroutKg)} kg total) [Floor: ${String.format(Locale.US, "%.1f", calc.floorGroutKg)} kg, Walls: ${String.format(Locale.US, "%.1f", calc.wallGroutKg)} kg]\n")
        } else {
            sb.append("Tile adhesive (${calc.adhesiveBrandName}): ${calc.adhesiveBags} bags (Floor bed ${calc.floorThicknessMm.toInt()} mm, Walls bed ${calc.wallThicknessMm.toInt()} mm, Skirting bed ${calc.skirtingThicknessMm.toInt()} mm)\n")
            sb.append("Tile grout: ${calc.groutBags} ${if (calc.groutBags == 1) "bag" else "bags"} (${calc.groutBagSizeKg} kg each, ${String.format(Locale.US, "%.1f", calc.totalGroutKg)} kg total at ${calc.groutDensity} density)\n")
        }

        if (calc.bondingLiquidLitres > 0) {
            sb.append("${calc.bondingLiquidResultText}\n")
        }
        if (calc.isTileChosen && calc.totalSpacersCount > 0) {
            if (calc.hasWallTiles && calc.wallSpacersCount > 0) {
                sb.append("Tile spacers: ${calc.totalSpacersCount} pieces [Floor (${calc.floorSpacerMm.toInt()} mm): ${calc.floorSpacersCount} pcs, Walls (${calc.wallSpacerMm.toInt()} mm): ${calc.wallSpacersCount} pcs]\n")
            } else {
                sb.append("Tile spacers (${calc.spacerSizeMm.toInt()} mm): ${calc.totalSpacersCount} pieces\n")
            }
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
