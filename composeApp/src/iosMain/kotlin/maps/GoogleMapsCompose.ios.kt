package maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.platform.LocalLayoutDirection
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.animateToCameraPosition
import cocoapods.GoogleMaps.animateToZoom
import kotlinx.cinterop.ExperimentalForeignApi
import maps.state.GoogleMapsState
import maps.state.GoogleMapsStateImpl
import maps.state.asImplement
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIEdgeInsetsMake


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMapsCompose(
    modifier: Modifier,
    googleMapsState: GoogleMapsState,
    mapsSettings: MapsSettings,
    onMarkerClick: (GoogleMapsMarker) -> Unit
) {

    val googleMapsView = remember { GMSMapView() }
    val gestureManager = remember { GestureManager() }

    val googleMapsDelegate = remember {
        IosGoogleMapsDelegate(
            stateImpl = googleMapsState as GoogleMapsStateImpl,
            gestureManager = gestureManager,
            onMarkerClick = onMarkerClick
        )
    }

    val initialCamera by googleMapsState.asImplement().initialCameraCoordinate.collectAsState()

    val moveCamera by googleMapsState.asImplement().moveCameraCoordinate.collectAsState()

    val zoomCamera by googleMapsState.asImplement().zoomCamera.collectAsState()
    val isNeedZoom by googleMapsState.asImplement().isNeedZoom.collectAsState()

    val markerList by googleMapsState.markerList.collectAsState()
    val selectedMarker by googleMapsState.asImplement().selectedMarker.collectAsState()

    val gesture by gestureManager.gesture.collectAsState()

    LaunchedEffect(gesture) {
        googleMapsState.asImplement().setMoveGesture(gesture)
    }

    LaunchedEffect(Unit) {
        googleMapsState.asImplement().setMapLoaded(false)
    }

    LaunchedEffect(googleMapsView) {
        googleMapsView.delegate = googleMapsDelegate
    }

    val layoutDirection = LocalLayoutDirection.current

    LaunchedEffect(mapsSettings) {
        googleMapsView.myLocationEnabled = mapsSettings.myLocationEnable
        googleMapsView.padding = mapsSettings.padding.run {
            UIEdgeInsetsMake(
                top = calculateTopPadding().value.toDouble(),
                left = calculateLeftPadding(layoutDirection).value.toDouble(),
                right = calculateRightPadding(layoutDirection).value.toDouble(),
                bottom = calculateBottomPadding().value.toDouble()
            )
        }
    }

    LaunchedEffect(mapsSettings) {
        googleMapsView.settings().myLocationButton = mapsSettings.myLocationButtonEnabled
    }

    LaunchedEffect(mapsSettings) {
        googleMapsView.settings.compassButton = mapsSettings.compassEnabled
    }

    LaunchedEffect(moveCamera) {
        if (!moveCamera.isZeroCoordinate()) {
            val cameraPosition = GMSCameraPosition(
                latitude = moveCamera.coordinate.latitude,
                longitude = moveCamera.coordinate.longitude,
                zoom = moveCamera.zoomWithDefault()
            )
            googleMapsView.animateToCameraPosition(cameraPosition)
        }
    }

    LaunchedEffect(zoomCamera) {
        if (isNeedZoom) {
            googleMapsView.animateToZoom(zoomCamera)
        }
    }

    LaunchedEffect(markerList, selectedMarker) {
        googleMapsView.clear()
        googleMapsView.selectedMarker = null
        for (marker in markerList) {
            val gmsMarker = GMSMarker()
            gmsMarker.setPosition(
                CLLocationCoordinate2DMake(
                    latitude = marker.coordinate.latitude,
                    longitude = marker.coordinate.longitude
                )
            )

            gmsMarker.title = marker.title
            gmsMarker.map = googleMapsView

            if (selectedMarker?.coordinate.toString() == marker.coordinate.toString()) {
                googleMapsView.selectedMarker = gmsMarker
            }
        }
    }

    UIKitView(
        factory = {
            googleMapsView
        },
        modifier = modifier,
        update = {
            val cameraPosition = GMSCameraPosition(
                latitude = initialCamera.coordinate.latitude,
                longitude = initialCamera.coordinate.longitude,
                zoom = initialCamera.zoomWithDefault()
            )
            it.camera = cameraPosition
        }
    )
}