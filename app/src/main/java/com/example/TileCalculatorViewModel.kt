package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.JobRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    JOBS_LIST,
    JOB_DETAIL,
    AREA_EDITOR,
    QUOTATION_VIEW,
    SETTINGS
}

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
    val currentScreen: AppScreen = AppScreen.JOBS_LIST,
    val jobs: List<Job> = emptyList(),
    val activeJob: Job? = null,
    val calculatedResult: JobCalculatedResult? = null,
    val activeArea: AreaItem? = null,
    val activeAreaIndex: Int? = null,
    val userProfile: UserProfile = UserProfile(),

    // Quotation
    val quotationDetailed: Boolean = true,
    val quotationText: String = "",

    // Messages & Dialogs
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val jobToDelete: Job? = null,
    val jobToRename: Job? = null,
    val renameInput: String = "",
    val areaIndexToDelete: Int? = null,

    // Legacy compatibility fields
    val roomLength: String = "3.5",
    val roomWidth: String = "1.8",
    val selectedTileSizeIndex: Int = 2,
    val customTileLength: String = "",
    val customTileWidth: String = "",
    val tilesPerBox: String = "12",
    val wasteAllowance: Double = 10.0,
    val isAdvancedOpen: Boolean = false,
    val adhesiveCoverage: String = "4.0",
    val groutCoverage: String = "10.0",
    val bondingCoverage: String = "5.0",
    val result: TileCalculationResult? = null
)

class TileCalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JobRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(TileCalculatorUiState())
    val uiState: StateFlow<TileCalculatorUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val loadedJobs = repository.loadJobs()
        val profile = repository.loadProfile()
        val activeId = repository.getActiveJobId()
        val active = loadedJobs.find { it.id == activeId } ?: loadedJobs.firstOrNull()
        val calc = active?.let { TileCalculatorEngine.calculateJob(it) }

        _uiState.update {
            it.copy(
                jobs = loadedJobs,
                activeJob = active,
                calculatedResult = calc,
                userProfile = profile
            )
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (screen == AppScreen.QUOTATION_VIEW && _uiState.value.activeJob != null) {
            refreshQuotationText()
        }
        _uiState.update { it.copy(currentScreen = screen, errorMessage = null, infoMessage = null) }
    }

    fun navigateBack() {
        _uiState.update { state ->
            val nextScreen = when (state.currentScreen) {
                AppScreen.AREA_EDITOR -> AppScreen.JOB_DETAIL
                AppScreen.QUOTATION_VIEW -> AppScreen.JOB_DETAIL
                AppScreen.SETTINGS -> AppScreen.JOBS_LIST
                AppScreen.JOB_DETAIL -> AppScreen.JOBS_LIST
                AppScreen.JOBS_LIST -> AppScreen.JOBS_LIST
            }
            state.copy(
                currentScreen = nextScreen,
                activeArea = null,
                activeAreaIndex = null,
                errorMessage = null
            )
        }
    }

    fun createNewJob() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateToday = dateFormat.format(Date())

        val newJob = Job(
            id = UUID.randomUUID().toString(),
            jobName = "New Tiling Job",
            clientName = "",
            siteAddress = "",
            notes = "",
            dateString = dateToday,
            unit = MeasurementUnit.METERS,
            isTileChosen = false, // Starts as Measured only
            areas = listOf(
                AreaItem(name = "Room 1", type = AreaType.ROOM_FLOOR, lengthInput = "4.0", widthInput = "3.0")
            )
        )

        val updatedList = listOf(newJob) + _uiState.value.jobs
        val calc = TileCalculatorEngine.calculateJob(newJob)

        repository.saveJobs(updatedList)
        repository.setActiveJobId(newJob.id)

        _uiState.update {
            it.copy(
                jobs = updatedList,
                activeJob = newJob,
                calculatedResult = calc,
                currentScreen = AppScreen.JOB_DETAIL
            )
        }
    }

    fun openJob(job: Job) {
        val calc = TileCalculatorEngine.calculateJob(job)
        repository.setActiveJobId(job.id)
        _uiState.update {
            it.copy(
                activeJob = job,
                calculatedResult = calc,
                currentScreen = AppScreen.JOB_DETAIL,
                errorMessage = null
            )
        }
    }

    fun updateActiveJob(updateLambda: (Job) -> Job) {
        val current = _uiState.value.activeJob ?: return
        val updated = updateLambda(current)
        val calc = TileCalculatorEngine.calculateJob(updated)
        val allJobs = _uiState.value.jobs.map { if (it.id == updated.id) updated else it }

        repository.saveJobs(allJobs)
        _uiState.update {
            it.copy(
                activeJob = updated,
                jobs = allJobs,
                calculatedResult = calc
            )
        }
    }

    fun saveJobExplicitly() {
        val current = _uiState.value.activeJob ?: return
        val allJobs = _uiState.value.jobs.map { if (it.id == current.id) current else it }
        repository.saveJobs(allJobs)
        _uiState.update { it.copy(infoMessage = "Job saved to device") }
    }

    // Job CRUD dialogs
    fun requestDeleteJob(job: Job) {
        _uiState.update { it.copy(jobToDelete = job) }
    }

    fun confirmDeleteJob() {
        val toDelete = _uiState.value.jobToDelete ?: return
        val updatedList = _uiState.value.jobs.filter { it.id != toDelete.id }
        repository.saveJobs(updatedList)

        val nextActive = updatedList.firstOrNull()
        val nextCalc = nextActive?.let { TileCalculatorEngine.calculateJob(it) }
        repository.setActiveJobId(nextActive?.id)

        _uiState.update {
            it.copy(
                jobs = updatedList,
                activeJob = nextActive,
                calculatedResult = nextCalc,
                jobToDelete = null,
                currentScreen = AppScreen.JOBS_LIST
            )
        }
    }

    fun dismissDeleteJobDialog() {
        _uiState.update { it.copy(jobToDelete = null) }
    }

    fun requestRenameJob(job: Job) {
        _uiState.update { it.copy(jobToRename = job, renameInput = job.jobName) }
    }

    fun onRenameInputChange(value: String) {
        _uiState.update { it.copy(renameInput = value) }
    }

    fun confirmRenameJob() {
        val target = _uiState.value.jobToRename ?: return
        val newName = _uiState.value.renameInput.trim().ifBlank { target.jobName }
        val updated = target.copy(jobName = newName)
        val allJobs = _uiState.value.jobs.map { if (it.id == updated.id) updated else it }

        repository.saveJobs(allJobs)
        _uiState.update {
            it.copy(
                jobs = allJobs,
                activeJob = if (it.activeJob?.id == updated.id) updated else it.activeJob,
                jobToRename = null,
                renameInput = ""
            )
        }
    }

    fun dismissRenameDialog() {
        _uiState.update { it.copy(jobToRename = null, renameInput = "") }
    }

    fun duplicateJob(job: Job) {
        val duplicate = job.copy(
            id = UUID.randomUUID().toString(),
            jobName = "${job.jobName} (Copy)"
        )
        val updated = listOf(duplicate) + _uiState.value.jobs
        repository.saveJobs(updated)
        _uiState.update { it.copy(jobs = updated, infoMessage = "Job duplicated") }
    }

    // Measurement unit
    fun setMeasurementUnit(unit: MeasurementUnit) {
        updateActiveJob { it.copy(unit = unit) }
    }

    // Area CRUD
    fun addNewArea(type: AreaType) {
        val defaultName = when (type) {
            AreaType.ROOM_FLOOR -> "Room ${(_uiState.value.activeJob?.areas?.size ?: 0) + 1}"
            AreaType.BATHROOM -> "Bathroom"
            AreaType.VERANDA -> "Veranda"
            AreaType.WALL_ONLY -> "Feature wall"
        }

        val newArea = AreaItem(
            id = UUID.randomUUID().toString(),
            name = defaultName,
            type = type,
            skirting = SkirtingConfig(
                enabled = type == AreaType.ROOM_FLOOR
            ),
            edgeStrip = EdgeStripConfig(
                enabled = true,
                material = if (type == AreaType.VERANDA) EdgeStripMaterial.METAL else EdgeStripMaterial.PLASTIC
            )
        )

        val updatedAreas = (_uiState.value.activeJob?.areas ?: emptyList()) + newArea
        updateActiveJob { it.copy(areas = updatedAreas) }

        _uiState.update {
            it.copy(
                activeArea = newArea,
                activeAreaIndex = updatedAreas.lastIndex,
                currentScreen = AppScreen.AREA_EDITOR
            )
        }
    }

    fun openAreaEditor(index: Int) {
        val currentJob = _uiState.value.activeJob ?: return
        val area = currentJob.areas.getOrNull(index) ?: return
        _uiState.update {
            it.copy(
                activeArea = area,
                activeAreaIndex = index,
                currentScreen = AppScreen.AREA_EDITOR
            )
        }
    }

    fun updateActiveArea(updateLambda: (AreaItem) -> AreaItem) {
        val currentArea = _uiState.value.activeArea ?: return
        val updated = updateLambda(currentArea)
        _uiState.update { it.copy(activeArea = updated) }
    }

    fun saveActiveArea() {
        val currentArea = _uiState.value.activeArea ?: return
        val index = _uiState.value.activeAreaIndex ?: return
        val currentJob = _uiState.value.activeJob ?: return

        val newAreas = currentJob.areas.toMutableList()
        if (index in newAreas.indices) {
            newAreas[index] = currentArea
        }

        updateActiveJob { it.copy(areas = newAreas) }
        _uiState.update {
            it.copy(
                activeArea = null,
                activeAreaIndex = null,
                currentScreen = AppScreen.JOB_DETAIL
            )
        }
    }

    fun duplicateArea(index: Int) {
        val currentJob = _uiState.value.activeJob ?: return
        val area = currentJob.areas.getOrNull(index) ?: return
        val duplicated = area.copy(
            id = UUID.randomUUID().toString(),
            name = "${area.name} (Copy)"
        )
        val newAreas = currentJob.areas.toMutableList().apply { add(index + 1, duplicated) }
        updateActiveJob { it.copy(areas = newAreas) }
    }

    fun requestDeleteArea(index: Int) {
        _uiState.update { it.copy(areaIndexToDelete = index) }
    }

    fun confirmDeleteArea() {
        val index = _uiState.value.areaIndexToDelete ?: return
        val currentJob = _uiState.value.activeJob ?: return
        val newAreas = currentJob.areas.toMutableList()
        if (index in newAreas.indices) {
            newAreas.removeAt(index)
        }
        updateActiveJob { it.copy(areas = newAreas) }
        _uiState.update { it.copy(areaIndexToDelete = null) }
    }

    fun dismissDeleteAreaDialog() {
        _uiState.update { it.copy(areaIndexToDelete = null) }
    }

    fun resetArea(index: Int) {
        val currentJob = _uiState.value.activeJob ?: return
        val oldArea = currentJob.areas.getOrNull(index) ?: return
        val reset = AreaItem(
            id = oldArea.id,
            name = oldArea.name,
            type = oldArea.type,
            lengthInput = "4.0",
            widthInput = "3.0"
        )
        val newAreas = currentJob.areas.toMutableList().apply { set(index, reset) }
        updateActiveJob { it.copy(areas = newAreas) }
        if (_uiState.value.activeAreaIndex == index) {
            _uiState.update { it.copy(activeArea = reset) }
        }
    }

    // Tile Choices
    fun toggleTileChosen(chosen: Boolean) {
        updateActiveJob { it.copy(isTileChosen = chosen) }
    }

    fun updateSharedFloorTile(updateLambda: (TileSpec) -> TileSpec) {
        updateActiveJob {
            it.copy(
                sharedFloorTileSpec = updateLambda(it.sharedFloorTileSpec),
                isTileChosen = true
            )
        }
    }

    fun updateSharedWallTile(updateLambda: (TileSpec) -> TileSpec) {
        updateActiveJob {
            it.copy(sharedWallTileSpec = updateLambda(it.sharedWallTileSpec))
        }
    }

    fun updateAdhesiveConfig(updateLambda: (AdhesiveConfig) -> AdhesiveConfig) {
        updateActiveJob {
            it.copy(adhesiveConfig = updateLambda(it.adhesiveConfig))
        }
    }

    // Quotation
    fun setQuotationDetailed(detailed: Boolean) {
        _uiState.update { it.copy(quotationDetailed = detailed) }
        refreshQuotationText()
    }

    fun updateQuotationText(text: String) {
        _uiState.update { it.copy(quotationText = text) }
    }

    private fun refreshQuotationText() {
        val job = _uiState.value.activeJob ?: return
        val profile = _uiState.value.userProfile
        val detailed = _uiState.value.quotationDetailed
        val text = TileCalculatorEngine.generateQuotation(job, profile, detailed)
        _uiState.update { it.copy(quotationText = text) }
    }

    // User Profile Settings
    fun updateUserProfile(name: String, phone: String) {
        val updated = UserProfile(businessName = name, phoneNumber = phone)
        repository.saveProfile(updated)
        _uiState.update { it.copy(userProfile = updated, infoMessage = "Details saved") }
    }

    fun dismissInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    // -------------------------------------------------------------
    // Legacy single-room compatibility methods for unit tests
    // -------------------------------------------------------------
    fun onRoomLengthChange(value: String) {
        _uiState.update { it.copy(roomLength = value, errorMessage = null) }
        updateActiveJob { job ->
            val first = job.areas.firstOrNull() ?: AreaItem()
            job.copy(areas = listOf(first.copy(lengthInput = value)))
        }
    }

    fun onRoomWidthChange(value: String) {
        _uiState.update { it.copy(roomWidth = value, errorMessage = null) }
        updateActiveJob { job ->
            val first = job.areas.firstOrNull() ?: AreaItem()
            job.copy(areas = listOf(first.copy(widthInput = value)))
        }
    }

    fun onTileSizeSelect(index: Int) {
        _uiState.update { it.copy(selectedTileSizeIndex = index, errorMessage = null) }
        val common = listOf(
            TileSpec("30 x 30 cm", 30.0, 30.0),
            TileSpec("40 x 40 cm", 40.0, 40.0),
            TileSpec("60 x 60 cm", 60.0, 60.0),
            TileSpec("60 x 120 cm", 60.0, 120.0),
            TileSpec("80 x 80 cm", 80.0, 80.0),
            TileSpec("Custom size", 50.0, 50.0, isCustom = true)
        )
        val spec = common.getOrElse(index) { common[2] }
        updateActiveJob { it.copy(sharedFloorTileSpec = spec, isTileChosen = true) }
    }

    fun onCustomTileLengthChange(value: String) {
        _uiState.update { it.copy(customTileLength = value, errorMessage = null) }
        val len = TileCalculatorEngine.parseNum(value)
        if (len > 0) {
            updateActiveJob {
                it.copy(
                    sharedFloorTileSpec = it.sharedFloorTileSpec.copy(lengthCm = len, isCustom = true),
                    isTileChosen = true
                )
            }
        }
    }

    fun onCustomTileWidthChange(value: String) {
        _uiState.update { it.copy(customTileWidth = value, errorMessage = null) }
        val wid = TileCalculatorEngine.parseNum(value)
        if (wid > 0) {
            updateActiveJob {
                it.copy(
                    sharedFloorTileSpec = it.sharedFloorTileSpec.copy(widthCm = wid, isCustom = true),
                    isTileChosen = true
                )
            }
        }
    }

    fun onTilesPerBoxChange(value: String) {
        _uiState.update { it.copy(tilesPerBox = value, errorMessage = null) }
        val count = value.trim().toIntOrNull() ?: 12
        updateActiveJob {
            it.copy(sharedFloorTileSpec = it.sharedFloorTileSpec.copy(tilesPerBox = count))
        }
    }

    fun onWasteAllowanceChange(value: Double) {
        _uiState.update { it.copy(wasteAllowance = value) }
        updateActiveJob {
            it.copy(sharedFloorTileSpec = it.sharedFloorTileSpec.copy(wastePercent = value))
        }
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
                selectedTileSizeIndex = 2,
                tilesPerBox = "12",
                wasteAllowance = 10.0,
                errorMessage = null
            )
        }
        calculate()
    }

    fun reset() {
        _uiState.update {
            it.copy(
                roomLength = "",
                roomWidth = "",
                result = null,
                errorMessage = null
            )
        }
    }

    fun calculate() {
        val state = _uiState.value
        val len = TileCalculatorEngine.parseNum(state.roomLength)
        if (len <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter your room length in meters (e.g. 3.5)") }
            return
        }
        val wid = TileCalculatorEngine.parseNum(state.roomWidth)
        if (wid <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter your room width in meters (e.g. 1.8)") }
            return
        }

        // Room Area
        val roomArea = len * wid
        val tileLenCm = if (state.customTileLength.isNotBlank()) TileCalculatorEngine.parseNum(state.customTileLength) else 60.0
        val tileWidCm = if (state.customTileWidth.isNotBlank()) TileCalculatorEngine.parseNum(state.customTileWidth) else 60.0
        val tileArea = (tileLenCm / 100.0) * (tileWidCm / 100.0)

        val rawTiles = (roomArea / tileArea) * (1.0 + state.wasteAllowance / 100.0)
        val tilesNeeded = kotlin.math.ceil(rawTiles).toInt()
        val boxCount = state.tilesPerBox.trim().toIntOrNull() ?: 12
        val boxesNeeded = kotlin.math.ceil(tilesNeeded.toDouble() / boxCount).toInt()
        val totalInBoxes = boxesNeeded * boxCount
        val spare = totalInBoxes - tilesNeeded

        val adhCov = TileCalculatorEngine.parseNum(state.adhesiveCoverage).let { if (it <= 0) 4.0 else it }
        val groutCov = TileCalculatorEngine.parseNum(state.groutCoverage).let { if (it <= 0) 10.0 else it }
        val bondCov = TileCalculatorEngine.parseNum(state.bondingCoverage).let { if (it <= 0) 5.0 else it }

        val res = TileCalculationResult(
            roomAreaSqM = roomArea,
            tileAreaSqM = tileArea,
            totalTilesNeeded = tilesNeeded,
            tilesPerBox = boxCount,
            boxesToBuy = boxesNeeded,
            totalTilesInBoxes = totalInBoxes,
            spareTiles = spare,
            adhesiveBags = kotlin.math.ceil(roomArea / adhCov).toInt(),
            groutBags = kotlin.math.ceil(roomArea / groutCov).toInt(),
            bondingLitres = kotlin.math.ceil(roomArea / bondCov).toInt(),
            wastePercent = state.wasteAllowance,
            tileLengthCm = tileLenCm,
            tileWidthCm = tileWidCm
        )

        _uiState.update { it.copy(result = res, errorMessage = null) }
    }
}
