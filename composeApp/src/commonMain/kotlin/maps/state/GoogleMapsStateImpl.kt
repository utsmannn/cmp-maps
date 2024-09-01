package maps.state

import entity.data.Coordinate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import maps.CameraCoordinate
import maps.GoogleMapsMarker
import maps.MoveGesture
import maps.isZeroCoordinate
import kotlin.coroutines.CoroutineContext

class GoogleMapsStateImpl(
    private val _initialCameraCoordinate: CameraCoordinate,
    private val _initialMarkerList: List<GoogleMapsMarker>
) : GoogleMapsState {

    private val _savedCameraCoordinate: MutableStateFlow<CameraCoordinate> = MutableStateFlow(
        CameraCoordinate()
    )
    override val cameraCoordinate: StateFlow<CameraCoordinate>
        get() = _savedCameraCoordinate

    private val _markerList: MutableStateFlow<List<GoogleMapsMarker>> = MutableStateFlow(
        _initialMarkerList
    )
    override val markerList: StateFlow<List<GoogleMapsMarker>>
        get() = _markerList

    private val _mapLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val mapLoaded: StateFlow<Boolean>
        get() = _mapLoaded

    private val _gesture: MutableStateFlow<MoveGesture> = MutableStateFlow(
        MoveGesture.MoveNotStarted
    )
    override val gesture: StateFlow<MoveGesture>
        get() = _gesture

    private val _initialCameraCoordinateFlow: MutableStateFlow<CameraCoordinate> get() = MutableStateFlow(_initialCameraCoordinate.copy())
    val initialCameraCoordinate: StateFlow<CameraCoordinate>
        get() = _initialCameraCoordinateFlow

    val zoomCamera: MutableStateFlow<Float> = MutableStateFlow(0f)
    val isNeedZoom: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val selectedMarker: MutableStateFlow<GoogleMapsMarker?> = MutableStateFlow(null)

    private val _moveCameraCoordinate: MutableStateFlow<CameraCoordinate> = MutableStateFlow(
        _initialCameraCoordinate.copy()
    )

    val moveCameraCoordinate: StateFlow<CameraCoordinate>
        get() = _moveCameraCoordinate

    private val scope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.Default
    }

    override fun animatedCamera(cameraCoordinate: CameraCoordinate) {
        val savedZoom = _savedCameraCoordinate.value.zoom
        val newCameraCoordinate = if (cameraCoordinate.zoom == null) {
            cameraCoordinate.copy(
                zoom = savedZoom
            )
        } else {
            cameraCoordinate
        }
        _moveCameraCoordinate.update { coor ->
            coor.copy(
                coordinate = newCameraCoordinate.coordinate,
                zoom = newCameraCoordinate.zoom,
                initializer = (1..500).random()
            )
        }
    }

    override fun zoomIn() {
        scope.launch {
            isNeedZoom.update { true }
            delay(60)
            val savedZoom = cameraCoordinate.value.zoomWithDefault()
            zoomCamera.update {
                savedZoom + 1
            }
            delay(60)
            isNeedZoom.update { false }
        }
    }

    override fun zoomOut() {
        scope.launch {
            isNeedZoom.update { true }
            delay(60)
            val savedZoom = cameraCoordinate.value.zoomWithDefault()
            zoomCamera.update {
                savedZoom - 1
            }
            delay(60)
            isNeedZoom.update { false }
        }
    }

    override fun addMarker(marker: GoogleMapsMarker) {
        _markerList.update { currentMarker ->
            if (!currentMarker.contains(marker)) {
                currentMarker + marker
            } else {
                currentMarker
            }
        }
    }

    override fun removeMarker(marker: GoogleMapsMarker) {
        _markerList.update { currentMarker ->
            if (currentMarker.contains(marker)) {
                currentMarker - marker
            } else {
                currentMarker
            }
        }
    }

    override fun setSelectedMarkerByCoordinate(coordinate: Coordinate) {
        val markerFound = _markerList.value.find {
            it.coordinate == coordinate
        }

        selectedMarker.update { markerFound }
    }

    override fun removeAllMarker() {
        _markerList.update { emptyList() }
    }

    fun saveCameraPosition(cameraCoordinate: CameraCoordinate) {
        _savedCameraCoordinate.update {
            cameraCoordinate
        }
    }

    fun setMapLoaded(mapLoaded: Boolean) {
        _mapLoaded.update { mapLoaded }
    }

    fun setMoveGesture(moveGesture: MoveGesture) {
        _gesture.update { moveGesture }
    }
}

fun GoogleMapsState.asImplement() = this as GoogleMapsStateImpl