package maps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import entity.data.Coordinate
import maps.state.GoogleMapsState
import maps.state.GoogleMapsStateImpl
import maps.state.asImplement


@Composable
actual fun GoogleMapsCompose(
    modifier: Modifier,
    googleMapsState: GoogleMapsState,
    mapsSettings: MapsSettings,
    onMarkerClick: (GoogleMapsMarker) -> Unit
) {

    val androidCameraPositionState = rememberCameraPositionState()

    val gestureManager = remember {
        GestureManager()
    }

    val initialCamera by googleMapsState.asImplement().initialCameraCoordinate.collectAsState()

    val moveCamera by googleMapsState.asImplement().moveCameraCoordinate.collectAsState()

    val zoomCamera by (googleMapsState as GoogleMapsStateImpl).zoomCamera.collectAsState()
    val isNeedZoom by (googleMapsState as GoogleMapsStateImpl).isNeedZoom.collectAsState()

    val selectedMarker by googleMapsState.asImplement().selectedMarker.collectAsState()

    val markerList by googleMapsState.markerList.collectAsState()

    val gesture by gestureManager.gesture.collectAsState()

    LaunchedEffect(gesture) {
        googleMapsState.asImplement().setMoveGesture(gesture)
    }

    LaunchedEffect(Unit) {
        googleMapsState.asImplement().setMapLoaded(false)
    }

    LaunchedEffect(initialCamera) {
        val latLng = LatLng(
            initialCamera.coordinate.latitude,
            initialCamera.coordinate.longitude
        )
        androidCameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(
                latLng, initialCamera.zoomWithDefault()
            )
        )
    }

    LaunchedEffect(moveCamera, mapsSettings) {
        if (!moveCamera.isZeroCoordinate()) {
            val latLng = LatLng(
                moveCamera.coordinate.latitude,
                moveCamera.coordinate.longitude
            )
            androidCameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    latLng, moveCamera.zoomWithDefault()
                )
            )
        }
    }

    LaunchedEffect(androidCameraPositionState.position) {
        val position = androidCameraPositionState.position
        val zoom = position.zoom
        val coordinate = Coordinate(
            latitude = position.target.latitude,
            longitude = position.target.longitude
        )

        val cameraCoordinate = CameraCoordinate(coordinate, zoom)

        val stateImpl = googleMapsState as GoogleMapsStateImpl
        stateImpl.saveCameraPosition(cameraCoordinate)
        gestureManager.setCoordinate(coordinate)
    }

    LaunchedEffect(zoomCamera) {
        if (isNeedZoom) {
            androidCameraPositionState.animate(
                CameraUpdateFactory.zoomTo(zoomCamera)
            )
        }
    }

    LaunchedEffect(androidCameraPositionState.isMoving) {
        val isGestureReason = androidCameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE
        val isMoveFromInput = androidCameraPositionState.isMoving && isGestureReason

        gestureManager.setIsMoving(isMoveFromInput)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = androidCameraPositionState,
        onMapLoaded = {
            googleMapsState.asImplement().setMapLoaded(true)
        },
        properties = MapProperties(
            isMyLocationEnabled = mapsSettings.myLocationEnable,
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = mapsSettings.myLocationButtonEnabled,
            compassEnabled = mapsSettings.compassEnabled
        ),
        contentPadding = mapsSettings.padding
    ) {

        for (marker in markerList) {
            val markerState = rememberMarkerState(
                position = LatLng(
                    marker.coordinate.latitude,
                    marker.coordinate.longitude
                )
            )

            LaunchedEffect(selectedMarker) {
                if (markerState.position.asString() == selectedMarker?.coordinate.toString()) {
                    markerState.showInfoWindow()
                } else {
                    markerState.hideInfoWindow()
                }
            }

            Marker(
                state = markerState,
                title = marker.title,
                onClick = { androidMarker ->
                    val googleMapsMarker = markerList.find {
                        println("marker: ${it.coordinate} | android marker: ${androidMarker.position
                            .asString()}")
                        it.coordinate.toString() == androidMarker.position
                            .asString()
                    }
                    if (googleMapsMarker != null) {
                        println("marker: not null -> $googleMapsMarker")
                        onMarkerClick.invoke(googleMapsMarker)
                        androidMarker.showInfoWindow()
                    }
                    true
                }
            )
        }

    }
}

fun LatLng.asString(): String {
    return "$latitude,$longitude"
}