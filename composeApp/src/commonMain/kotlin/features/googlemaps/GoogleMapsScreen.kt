package features.googlemaps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import entity.data.Coordinate
import maps.CameraCoordinate
import maps.GoogleMapsCompose
import maps.GoogleMapsMarker
import maps.MapsSettings
import maps.rememberLocationService
import maps.state.rememberGoogleMapsState
import navigation.LocalNavigator
import navigation.NavTarget

val pamulangCoordinate = Coordinate(-6.34245001897142, 106.69354203471914)
val monasCoordinate = Coordinate(-6.176197222612754, 106.827467801915)

@Composable
fun GoogleMapsScreen() {

    val navigator = LocalNavigator.current

    val locationService = rememberLocationService()

    val googleMapsState = rememberGoogleMapsState(
        initialCameraCoordinate = CameraCoordinate(
            coordinate = Coordinate(),
            zoom = 16f
        )
    )

    val savedCameraCoordinate by googleMapsState.cameraCoordinate.collectAsState()
    val isMapLoaded by googleMapsState.mapLoaded.collectAsState()

    val gesture by googleMapsState.gesture.collectAsState()

    LaunchedEffect(savedCameraCoordinate) {
        println("camera on ---> coordinate: ${savedCameraCoordinate.coordinate} | zoom: ${savedCameraCoordinate.zoom}")
    }

    val myLocation by locationService.myLocation.collectAsState()

    LaunchedEffect(myLocation, isMapLoaded) {
        println("my location on screen: $myLocation")
        if (isMapLoaded) {
            googleMapsState.animatedCamera(
                cameraCoordinate = CameraCoordinate(
                    coordinate = myLocation
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        locationService.getMyLocation()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMapsCompose(
            modifier = Modifier.fillMaxSize(),
            googleMapsState = googleMapsState,
            mapsSettings = MapsSettings(
                myLocationEnable = myLocation.latitude != 0.0,
                compassEnabled = true,
                myLocationButtonEnabled = true
            )
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gesture.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.background(color = Color.Yellow)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    googleMapsState.animatedCamera(
                        CameraCoordinate(
                            coordinate = pamulangCoordinate,
                            zoom = 12f
                        )
                    )

                    googleMapsState.addMarker(
                        GoogleMapsMarker(
                            coordinate = pamulangCoordinate
                        )
                    )
                }
            ) {
                Text("move camera and add marker to pamulang")
            }

            Button(
                onClick = {
                    googleMapsState.animatedCamera(
                        CameraCoordinate(
                            coordinate = monasCoordinate
                        )
                    )

                    googleMapsState.addMarker(
                        GoogleMapsMarker(
                            coordinate = monasCoordinate,
                            title = "monas"
                        )
                    )
                }
            ) {
                Text("move camera and add marker to monas")
            }

            Button(
                onClick = {
                    googleMapsState.removeMarker(
                        GoogleMapsMarker(
                            coordinate = monasCoordinate,
                            title = "monas"
                        )
                    )
                }
            ) {
                Text("remove marker monas")
            }

            Button(
                onClick = {
                    googleMapsState.zoomIn()
                }
            ) {
                Text("zoom in")
            }

            Button(
                onClick = {
                    googleMapsState.zoomOut()
                }
            ) {
                Text("zoom out")
            }

            Button(
                onClick = {
                    navigator.navigate(NavTarget.SearchLocation)
                }
            ) {
                Text("navigate to search")
            }
        }
    }
}