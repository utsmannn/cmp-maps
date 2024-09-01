package maps.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import entity.data.Coordinate
import kotlinx.coroutines.flow.StateFlow
import maps.CameraCoordinate
import maps.GoogleMapsMarker
import maps.MoveGesture

interface GoogleMapsState {
    val cameraCoordinate: StateFlow<CameraCoordinate>
    val markerList: StateFlow<List<GoogleMapsMarker>>

    val mapLoaded: StateFlow<Boolean>

    val gesture: StateFlow<MoveGesture>

    fun animatedCamera(cameraCoordinate: CameraCoordinate)

    fun zoomIn()
    fun zoomOut()

    fun addMarker(marker: GoogleMapsMarker)
    fun removeMarker(marker: GoogleMapsMarker)
    fun setSelectedMarkerByCoordinate(coordinate: Coordinate)
    fun removeAllMarker()

    companion object {
        val Saver: Saver<GoogleMapsState, GoogleMapsStateSaveable> = Saver(
            save = {
                val cameraCoordinate = it.cameraCoordinate.value
                val markerList = it.markerList.value

                GoogleMapsStateSaveable(cameraCoordinate, markerList)
            },
            restore = {
                val initialCameraCoordinate = it.cameraCoordinate
                val initialMarkerList = it.markerList
                GoogleMapsStateImpl(
                    _initialCameraCoordinate = initialCameraCoordinate,
                    _initialMarkerList = initialMarkerList
                )
            }
        )
    }

    @Parcelize
    data class GoogleMapsStateSaveable(
        val cameraCoordinate: CameraCoordinate,
        val markerList: List<GoogleMapsMarker>
    ) : Parcelable
}

@Composable
fun rememberGoogleMapsState(initialCameraCoordinate: CameraCoordinate): GoogleMapsState {
    return rememberSaveable(saver = GoogleMapsState.Saver) {
        GoogleMapsStateImpl(initialCameraCoordinate, emptyList())
    }
}