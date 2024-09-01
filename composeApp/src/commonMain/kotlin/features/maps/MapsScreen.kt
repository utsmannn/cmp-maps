package features.maps

import BackPress
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import authentication.User
import authentication.rememberGoogleAuthentication
import base.State
import com.seiko.imageloader.rememberImagePainter
import entity.data.Place
import features.maps.component.CarouselHeight
import features.maps.component.CarouselPlaces
import features.maps.component.ItemPlace
import features.maps.component.SearchBarPlace
import isAndroid
import isKeyboardOpen
import maps.CameraCoordinate
import maps.GoogleMapsCompose
import maps.GoogleMapsMarker
import maps.MapsSettings
import maps.rememberLocationService
import maps.state.rememberGoogleMapsState
import navigation.LocalNavigator
import navigation.NavTarget
import onFailure
import onLoading
import onSuccess

@Composable
fun MapsScreen(
    viewModel: MapsViewModel = viewModel { MapsViewModel() }
) {

    val mapsState = rememberGoogleMapsState(
        initialCameraCoordinate = CameraCoordinate(
            zoom = 16f
        )
    )

    val locationService = rememberLocationService()

    val isMapHasLoaded by mapsState.mapLoaded.collectAsState()
    val myLocation by locationService.myLocation.collectAsState()

    val model by viewModel.stateModel.collectAsState()

    val googleAuthentication = rememberGoogleAuthentication()

    val user by googleAuthentication.user

    val isSignIn by googleAuthentication.isSignedIn

    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        locationService.getMyLocation()
        viewModel.handleIntent(MapsIntent.ObserverQuery)
        googleAuthentication.checkIsSignIn()
    }

    LaunchedEffect(myLocation) {
        viewModel.handleIntent(
            MapsIntent.SetMyCoordinate(myLocation)
        )
    }

    LaunchedEffect(myLocation, isMapHasLoaded) {
        if (isMapHasLoaded) {
            mapsState.animatedCamera(
                cameraCoordinate = CameraCoordinate(
                    coordinate = myLocation
                )
            )
        }
    }

    LaunchedEffect(model.selectedPlace) {
        if (model.selectedPlace != Place.Empty) {
            mapsState.animatedCamera(
                cameraCoordinate = CameraCoordinate(
                    coordinate = model.selectedPlace.coordinate,
                    zoom = 18f
                )
            )

            mapsState.setSelectedMarkerByCoordinate(model.selectedPlace.coordinate)
        }
    }

    val places by remember(model.placesState) {
        derivedStateOf {
            val placeState = model.placesState
            if (placeState is State.Success) {
                placeState.data
            } else {
                emptyList()
            }
        }
    }

    BackPress(true) {
        when {
            model.isShowSearch -> {
                viewModel.handleIntent(
                    MapsIntent.SetIsShowSearch(false)
                )
            }

            places.isNotEmpty() -> {
                viewModel.handleIntent(
                    MapsIntent.SetPlacesClear
                )
                mapsState.removeAllMarker()
            }

            else -> {
                navigator.quit()
            }
        }
    }

    val imePadding by rememberUpdatedState(
        WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    )
    val systemGesture by rememberUpdatedState(
        WindowInsets.systemGestures.asPaddingValues().calculateBottomPadding()
    )

    val isKeyboardOpen by isKeyboardOpen()

    LaunchedEffect(places, imePadding, systemGesture, isKeyboardOpen) {
        val platformSystemGesture = if (isKeyboardOpen) {
            systemGesture
        } else {
            if (isAndroid) {
                0.dp
            } else {
                systemGesture
            }
        }

        val bottomMapsPadding = when {
            isKeyboardOpen -> imePadding - platformSystemGesture
            places.isNotEmpty() -> (CarouselHeight + 16.dp) - platformSystemGesture
            else -> 0.dp
        }

        viewModel.handleIntent(
            MapsIntent.SetBottomMapPadding(bottomMapsPadding)
        )
    }

    if (model.showProfileDialog) {
        DialogProfileContent(
            onDismiss = {
                viewModel.handleIntent(
                    MapsIntent.DismissProfileDialog
                )
            },
            onClickSignOut = {
                googleAuthentication.signOut()
            },
            user = user
        )
    }

    LaunchedEffect(isSignIn, model.showProfileDialog) {
        if (!isSignIn) {
            if (model.showProfileDialog) {
              viewModel.handleIntent(
                  MapsIntent.DismissProfileDialog
              )
            }

            navigator.newRoot(NavTarget.SignIn)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        GoogleMapsCompose(
            modifier = Modifier.fillMaxSize(),
            googleMapsState = mapsState,
            mapsSettings = MapsSettings(
                myLocationEnable = myLocation.latitude != 0.0,
                compassEnabled = true,
                padding = PaddingValues(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = model.mapBottomPadding
                )
            ),
            onMarkerClick = { marker ->
                viewModel.handleIntent(
                    MapsIntent.SetSelectedMarker(marker)
                )
            }
        )

        CarouselPlaces(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            places = places,
            selectedPlace = model.selectedPlace,
            onPageChange = { place ->
                viewModel.handleIntent(
                    MapsIntent.SetSelectedPlace(place)
                )
            }
        )

        Box(
            modifier = Modifier
        ) {

            SearchBarPlace(
                value = model.query,
                onEditValue = {
                    viewModel.handleIntent(
                        MapsIntent.SetQuery(it)
                    )
                    viewModel.handleIntent(
                        MapsIntent.SetIsShowSearch(true)
                    )
                },
                isShowSearch = model.isShowSearch,
                onDoneEdit = {
                    viewModel.handleIntent(
                        MapsIntent.SetIsShowSearch(false)
                    )
                },
                isPlaceNotEmpty = places.isNotEmpty(),
                photoUrl = user?.photoUrl,
                onBackButtonClick = {
                    if (!model.isShowSearch) {
                        viewModel.handleIntent(
                            MapsIntent.SetPlacesClear
                        )
                        mapsState.removeAllMarker()
                    } else {
                        viewModel.handleIntent(
                            MapsIntent.SetIsShowSearch(false)
                        )
                    }
                },
                onIconProfileClick = {
                    viewModel.handleIntent(
                        MapsIntent.ShowProfileDialog
                    )
                },
                content = { keyboardController ->
                    with(model.placesState) {
                        onLoading {
                            CircularProgressIndicator()
                        }
                        onFailure {
                            Text(
                                text = it.message.orEmpty(),
                                color = Color.Red
                            )
                        }
                        onSuccess { places ->
                            LaunchedEffect(places) {
                                for (place in places) {
                                    mapsState.addMarker(
                                        marker = GoogleMapsMarker(
                                            coordinate = place.coordinate,
                                            title = place.name
                                        )
                                    )
                                }
                            }
                            for (place in places) {
                                ItemPlace(place) {
                                    keyboardController?.hide()
                                    viewModel.handleIntent(
                                        MapsIntent.SetIsShowSearch(false)
                                    )
                                    viewModel.handleIntent(
                                        MapsIntent.SetSelectedPlace(place)
                                    )
                                }
                            }
                        }
                    }
                }
            )

        }
    }
}

@Composable
fun DialogProfileContent(
    onDismiss: () -> Unit,
    onClickSignOut: () -> Unit,
    user: User?
) {
    if (user != null) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val painter = rememberImagePainter(user.photoUrl.orEmpty())
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = user.name,
                    style = TextStyle.Default
                        .copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = user.email,
                    style = TextStyle.Default
                        .copy(
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        onClickSignOut.invoke()
                    },
                    colors = ButtonDefaults
                        .buttonColors(
                            backgroundColor = Color.White
                        ),
                    border = BorderStroke(width = 1.dp, color = Color.Black.copy(alpha = 0.5f))
                ) {
                    Text("Sign Out")
                }
            }
        }
    }
}
