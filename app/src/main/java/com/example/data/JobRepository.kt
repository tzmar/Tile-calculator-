package com.example.data

import android.content.Context
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JobRepository(private val context: Context) {

    private val jobsFile = File(context.filesDir, "jobs_store.json")
    private val profileFile = File(context.filesDir, "user_profile.json")
    private val prefs = context.getSharedPreferences("tile_calc_prefs", Context.MODE_PRIVATE)

    fun loadJobs(): List<Job> {
        if (!jobsFile.exists()) {
            // Seed with a default example job so the user immediately understands how it works
            val defaultJob = createDefaultSampleJob()
            saveJobs(listOf(defaultJob))
            return listOf(defaultJob)
        }

        return try {
            val jsonStr = jobsFile.readText()
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<Job>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(deserializeJob(obj))
            }
            if (list.isEmpty()) {
                val sample = createDefaultSampleJob()
                saveJobs(listOf(sample))
                listOf(sample)
            } else list
        } catch (e: Exception) {
            e.printStackTrace()
            val sample = createDefaultSampleJob()
            listOf(sample)
        }
    }

    fun saveJobs(jobs: List<Job>) {
        try {
            val jsonArray = JSONArray()
            jobs.forEach { job ->
                jsonArray.put(serializeJob(job))
            }
            jobsFile.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadProfile(): UserProfile {
        if (!profileFile.exists()) {
            return UserProfile(businessName = "Thabo Builders", phoneNumber = "71 234 567")
        }
        return try {
            val obj = JSONObject(profileFile.readText())
            UserProfile(
                businessName = obj.optString("businessName", "Thabo Builders"),
                phoneNumber = obj.optString("phoneNumber", "71 234 567")
            )
        } catch (e: Exception) {
            UserProfile(businessName = "Thabo Builders", phoneNumber = "71 234 567")
        }
    }

    fun saveProfile(profile: UserProfile) {
        try {
            val obj = JSONObject().apply {
                put("businessName", profile.businessName)
                put("phoneNumber", profile.phoneNumber)
            }
            profileFile.writeText(obj.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getActiveJobId(): String? = prefs.getString("active_job_id", null)

    fun setActiveJobId(id: String?) {
        prefs.edit().putString("active_job_id", id).apply()
    }

    fun createDefaultSampleJob(): Job {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateToday = dateFormat.format(Date())

        val mainBedroom = AreaItem(
            name = "Main bedroom",
            type = AreaType.ROOM_FLOOR,
            lengthInput = "4.0",
            widthInput = "3.0",
            doorwayCount = 1,
            doorwayWidthInput = "0.9",
            skirting = SkirtingConfig(enabled = true, heightCm = 10.0, method = SkirtingMethod.CUT_FROM_FLOOR),
            edgeStrip = EdgeStripConfig(enabled = true, material = EdgeStripMaterial.PLASTIC)
        )

        val bathroom = AreaItem(
            name = "Guest bathroom",
            type = AreaType.BATHROOM,
            lengthInput = "2.8",
            widthInput = "2.0",
            doorwayCount = 1,
            doorwayWidthInput = "0.8",
            bathroomWall = BathroomWallConfig(
                enabled = true,
                wallHeightM = 2.1,
                tiledToCeiling = false,
                doorCount = 1,
                doorWidthM = 0.8,
                doorHeightM = 2.0,
                windowCount = 1,
                windowWidthM = 0.6,
                windowHeightM = 0.6
            ),
            bathTub = BathTubConfig(
                enabled = true,
                lengthM = 1.7,
                widthM = 0.75,
                heightM = 0.55,
                tileFront = true,
                tileBack = false,
                tileLeft = true,
                tileRight = true,
                tileTopRim = true,
                hollowLengthM = 1.4,
                hollowWidthM = 0.55
            ),
            skirting = SkirtingConfig(enabled = false),
            edgeStrip = EdgeStripConfig(enabled = true, material = EdgeStripMaterial.PLASTIC)
        )

        val veranda = AreaItem(
            name = "Front veranda",
            type = AreaType.VERANDA,
            lengthInput = "5.0",
            widthInput = "2.2",
            verandaRaised = true,
            verandaRaisedHeightInput = "0.3",
            verandaSideFront = true,
            verandaSideBack = false,
            verandaSideLeft = true,
            verandaSideRight = true,
            verandaTrimVerticalCorners = true,
            skirting = SkirtingConfig(enabled = false),
            edgeStrip = EdgeStripConfig(enabled = true, material = EdgeStripMaterial.METAL)
        )

        return Job(
            jobName = "Mr Kgosi - 3 bedroom house",
            clientName = "Mr Kgosi",
            siteAddress = "Plot 1234, Gaborone",
            notes = "Client prefers neutral porcelain tiles. Check veranda slope.",
            dateString = dateToday,
            unit = MeasurementUnit.METERS,
            isTileChosen = true,
            sharedFloorTileSpec = TileSpec(sizeLabel = "60 x 60 cm", lengthCm = 60.0, widthCm = 60.0, tilesPerBox = 12, wastePercent = 10.0, spacerMm = 3.0),
            sameTileForAllAreas = true,
            sameTileForWallsAndSides = true,
            areas = listOf(mainBedroom, bathroom, veranda)
        )
    }

    private fun serializeJob(job: Job): JSONObject {
        val obj = JSONObject()
        obj.put("id", job.id)
        obj.put("jobName", job.jobName)
        obj.put("clientName", job.clientName)
        obj.put("siteAddress", job.siteAddress)
        obj.put("notes", job.notes)
        obj.put("dateString", job.dateString)
        obj.put("unit", job.unit.name)
        obj.put("isTileChosen", job.isTileChosen)
        obj.put("sameTileForAllAreas", job.sameTileForAllAreas)
        obj.put("sameTileForWallsAndSides", job.sameTileForWallsAndSides)

        // Tile Specs
        obj.put("floorTileSpec", serializeTileSpec(job.sharedFloorTileSpec))
        obj.put("wallTileSpec", serializeTileSpec(job.sharedWallTileSpec))

        // Adhesive
        val adh = job.adhesiveConfig
        val adhObj = JSONObject().apply {
            put("brand", adh.brand.name)
            put("otherFloorCov", adh.otherFloorCoverage)
            put("otherWallCov", adh.otherWallCoverage)
            put("floorThicknessMm", adh.floorThicknessMm)
            put("wallThicknessMm", adh.wallThicknessMm)
            put("groutCov", adh.groutCoveragePerBag)
            put("includeBondingLiquid", adh.includeBondingLiquid)
            put("bondingLiquidMode", adh.bondingLiquidMode.name)
            put("bondingMlPerBag", adh.bondingMlPerBag)
            put("customBondingMl", adh.customBondingMl)
        }
        obj.put("adhesiveConfig", adhObj)

        // Areas
        val areasArray = JSONArray()
        job.areas.forEach { area ->
            areasArray.put(serializeArea(area))
        }
        obj.put("areas", areasArray)

        return obj
    }

    private fun serializeTileSpec(spec: TileSpec): JSONObject {
        return JSONObject().apply {
            put("sizeLabel", spec.sizeLabel)
            put("lengthCm", spec.lengthCm)
            put("widthCm", spec.widthCm)
            put("isCustom", spec.isCustom)
            put("tilesPerBox", spec.tilesPerBox)
            put("wastePercent", spec.wastePercent)
            put("spacerMm", spec.spacerMm)
        }
    }

    private fun serializeArea(area: AreaItem): JSONObject {
        return JSONObject().apply {
            put("id", area.id)
            put("name", area.name)
            put("type", area.type.name)
            put("note", area.note)
            put("lengthInput", area.lengthInput)
            put("widthInput", area.widthInput)
            put("doorwayCount", area.doorwayCount)
            put("doorwayWidthInput", area.doorwayWidthInput)

            // Bathroom wall
            val bw = area.bathroomWall
            put("bw_enabled", bw.enabled)
            put("bw_height", bw.wallHeightM)
            put("bw_tiledToCeiling", bw.tiledToCeiling)
            put("bw_doorCount", bw.doorCount)
            put("bw_doorWidth", bw.doorWidthM)
            put("bw_doorHeight", bw.doorHeightM)
            put("bw_windowCount", bw.windowCount)
            put("bw_windowWidth", bw.windowWidthM)
            put("bw_windowHeight", bw.windowHeightM)

            // Bath tub
            val bt = area.bathTub
            put("bt_enabled", bt.enabled)
            put("bt_length", bt.lengthM)
            put("bt_width", bt.widthM)
            put("bt_height", bt.heightM)
            put("bt_tileFront", bt.tileFront)
            put("bt_tileBack", bt.tileBack)
            put("bt_tileLeft", bt.tileLeft)
            put("bt_tileRight", bt.tileRight)
            put("bt_tileTopRim", bt.tileTopRim)
            put("bt_hollowLength", bt.hollowLengthM)
            put("bt_hollowWidth", bt.hollowWidthM)

            // Veranda
            put("v_raised", area.verandaRaised)
            put("v_raisedHeight", area.verandaRaisedHeightInput)
            put("v_front", area.verandaSideFront)
            put("v_back", area.verandaSideBack)
            put("v_left", area.verandaSideLeft)
            put("v_right", area.verandaSideRight)
            put("v_trimCorners", area.verandaTrimVerticalCorners)

            // Wall only
            put("wo_length", area.wallOnlyLengthInput)
            put("wo_height", area.wallOnlyHeightInput)
            put("wo_openings", area.wallOnlyOpeningsAreaInput)

            // Skirting
            val sk = area.skirting
            put("sk_enabled", sk.enabled)
            put("sk_heightCm", sk.heightCm)
            put("sk_customHeightCm", sk.customHeightCm)
            put("sk_customLength", sk.customLengthMeters)
            put("sk_method", sk.method.name)
            put("sk_readyMadePieceLen", sk.readyMadePieceLenCm)
            put("sk_readyMadePiecesBox", sk.readyMadePiecesPerBox)
            put("sk_finishWithEdgeStrip", sk.finishWithEdgeStrip)

            // Edge strip
            val es = area.edgeStrip
            put("es_enabled", es.enabled)
            put("es_material", es.material.name)
            put("es_customLength", es.customLengthMeters)
            put("es_pieceLength", es.pieceLengthMeters)
            put("es_thickness", es.tileThicknessMm)

            if (area.customTileSpec != null) {
                put("customTileSpec", serializeTileSpec(area.customTileSpec))
            }
        }
    }

    private fun deserializeJob(obj: JSONObject): Job {
        val unit = try {
            MeasurementUnit.valueOf(obj.optString("unit", MeasurementUnit.METERS.name))
        } catch (e: Exception) {
            MeasurementUnit.METERS
        }

        val floorSpec = deserializeTileSpec(obj.optJSONObject("floorTileSpec"))
        val wallSpec = deserializeTileSpec(obj.optJSONObject("wallTileSpec"))

        val adhObj = obj.optJSONObject("adhesiveConfig")
        val adhConfig = if (adhObj != null) {
            val brand = try {
                AdhesiveBrand.valueOf(adhObj.optString("brand", AdhesiveBrand.EASY_GRIP.name))
            } catch (e: Exception) {
                AdhesiveBrand.EASY_GRIP
            }
            val bMode = try {
                BondingLiquidMode.valueOf(adhObj.optString("bondingLiquidMode", BondingLiquidMode.ADDITIVE_SPLASH.name))
            } catch (e: Exception) {
                BondingLiquidMode.ADDITIVE_SPLASH
            }
            AdhesiveConfig(
                brand = brand,
                otherFloorCoverage = adhObj.optDouble("otherFloorCov", 3.5),
                otherWallCoverage = adhObj.optDouble("otherWallCov", 5.0),
                floorThicknessMm = adhObj.optDouble("floorThicknessMm", 5.0),
                wallThicknessMm = adhObj.optDouble("wallThicknessMm", 3.0),
                groutCoveragePerBag = adhObj.optDouble("groutCov", 10.0),
                includeBondingLiquid = adhObj.optBoolean("includeBondingLiquid", false),
                bondingLiquidMode = bMode,
                bondingMlPerBag = adhObj.optDouble("bondingMlPerBag", 250.0),
                customBondingMl = adhObj.optString("customBondingMl", "")
            )
        } else AdhesiveConfig()

        val areasList = mutableListOf<AreaItem>()
        val areasArray = obj.optJSONArray("areas")
        if (areasArray != null) {
            for (i in 0 until areasArray.length()) {
                val aObj = areasArray.getJSONObject(i)
                areasList.add(deserializeArea(aObj))
            }
        }

        return Job(
            id = obj.optString("id"),
            jobName = obj.optString("jobName"),
            clientName = obj.optString("clientName"),
            siteAddress = obj.optString("siteAddress"),
            notes = obj.optString("notes"),
            dateString = obj.optString("dateString"),
            unit = unit,
            isTileChosen = obj.optBoolean("isTileChosen", false),
            sameTileForAllAreas = obj.optBoolean("sameTileForAllAreas", true),
            sameTileForWallsAndSides = obj.optBoolean("sameTileForWallsAndSides", true),
            sharedFloorTileSpec = floorSpec,
            sharedWallTileSpec = wallSpec,
            adhesiveConfig = adhConfig,
            areas = areasList
        )
    }

    private fun deserializeTileSpec(obj: JSONObject?): TileSpec {
        if (obj == null) return TileSpec()
        return TileSpec(
            sizeLabel = obj.optString("sizeLabel", "60 x 60 cm"),
            lengthCm = obj.optDouble("lengthCm", 60.0),
            widthCm = obj.optDouble("widthCm", 60.0),
            isCustom = obj.optBoolean("isCustom", false),
            tilesPerBox = obj.optInt("tilesPerBox", 12),
            wastePercent = obj.optDouble("wastePercent", 10.0),
            spacerMm = obj.optDouble("spacerMm", 3.0)
        )
    }

    private fun deserializeArea(obj: JSONObject): AreaItem {
        val type = try {
            AreaType.valueOf(obj.optString("type", AreaType.ROOM_FLOOR.name))
        } catch (e: Exception) {
            AreaType.ROOM_FLOOR
        }

        val skMethod = try {
            SkirtingMethod.valueOf(obj.optString("sk_method", SkirtingMethod.CUT_FROM_FLOOR.name))
        } catch (e: Exception) {
            SkirtingMethod.CUT_FROM_FLOOR
        }

        val esMaterial = try {
            EdgeStripMaterial.valueOf(obj.optString("es_material", EdgeStripMaterial.PLASTIC.name))
        } catch (e: Exception) {
            EdgeStripMaterial.PLASTIC
        }

        val bw = BathroomWallConfig(
            enabled = obj.optBoolean("bw_enabled", false),
            wallHeightM = obj.optDouble("bw_height", 2.1),
            tiledToCeiling = obj.optBoolean("bw_tiledToCeiling", false),
            doorCount = obj.optInt("bw_doorCount", 1),
            doorWidthM = obj.optDouble("bw_doorWidth", 0.8),
            doorHeightM = obj.optDouble("bw_doorHeight", 2.0),
            windowCount = obj.optInt("bw_windowCount", 0),
            windowWidthM = obj.optDouble("bw_windowWidth", 0.6),
            windowHeightM = obj.optDouble("bw_windowHeight", 0.6)
        )

        val bt = BathTubConfig(
            enabled = obj.optBoolean("bt_enabled", false),
            lengthM = obj.optDouble("bt_length", 1.7),
            widthM = obj.optDouble("bt_width", 0.75),
            heightM = obj.optDouble("bt_height", 0.55),
            tileFront = obj.optBoolean("bt_tileFront", true),
            tileBack = obj.optBoolean("bt_tileBack", false),
            tileLeft = obj.optBoolean("bt_tileLeft", true),
            tileRight = obj.optBoolean("bt_tileRight", true),
            tileTopRim = obj.optBoolean("bt_tileTopRim", true),
            hollowLengthM = obj.optDouble("bt_hollowLength", 1.4),
            hollowWidthM = obj.optDouble("bt_hollowWidth", 0.55)
        )

        val sk = SkirtingConfig(
            enabled = obj.optBoolean("sk_enabled", true),
            heightCm = obj.optDouble("sk_heightCm", 10.0),
            customHeightCm = obj.optString("sk_customHeightCm", ""),
            customLengthMeters = obj.optString("sk_customLength", ""),
            method = skMethod,
            readyMadePieceLenCm = obj.optDouble("sk_readyMadePieceLen", 60.0),
            readyMadePiecesPerBox = obj.optInt("sk_readyMadePiecesBox", 10),
            finishWithEdgeStrip = obj.optBoolean("sk_finishWithEdgeStrip", true)
        )

        val es = EdgeStripConfig(
            enabled = obj.optBoolean("es_enabled", true),
            material = esMaterial,
            customLengthMeters = obj.optString("es_customLength", ""),
            pieceLengthMeters = obj.optDouble("es_pieceLength", 2.5),
            tileThicknessMm = obj.optInt("es_thickness", 10)
        )

        val customTile = if (obj.has("customTileSpec")) deserializeTileSpec(obj.optJSONObject("customTileSpec")) else null

        return AreaItem(
            id = obj.optString("id"),
            name = obj.optString("name", "Area"),
            type = type,
            note = obj.optString("note", ""),
            lengthInput = obj.optString("lengthInput", "4.0"),
            widthInput = obj.optString("widthInput", "3.0"),
            doorwayCount = obj.optInt("doorwayCount", 0),
            doorwayWidthInput = obj.optString("doorwayWidthInput", "0.9"),
            bathroomWall = bw,
            bathTub = bt,
            verandaRaised = obj.optBoolean("v_raised", true),
            verandaRaisedHeightInput = obj.optString("v_raisedHeight", "0.3"),
            verandaSideFront = obj.optBoolean("v_front", true),
            verandaSideBack = obj.optBoolean("v_back", false),
            verandaSideLeft = obj.optBoolean("v_left", true),
            verandaSideRight = obj.optBoolean("v_right", true),
            verandaTrimVerticalCorners = obj.optBoolean("v_trimCorners", true),
            wallOnlyLengthInput = obj.optString("wo_length", "4.0"),
            wallOnlyHeightInput = obj.optString("wo_height", "2.4"),
            wallOnlyOpeningsAreaInput = obj.optString("wo_openings", "0.0"),
            skirting = sk,
            edgeStrip = es,
            customTileSpec = customTile
        )
    }
}
