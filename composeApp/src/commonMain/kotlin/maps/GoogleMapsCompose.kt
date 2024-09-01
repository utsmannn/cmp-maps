package maps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import maps.state.GoogleMapsState

@Composable
expect fun GoogleMapsCompose(
    modifier: Modifier,
    googleMapsState: GoogleMapsState,
    mapsSettings: MapsSettings = MapsSettings(),
    onMarkerClick: (GoogleMapsMarker) -> Unit = {}
)

val DefaultMapsPadding: PaddingValues
    @Composable
    get() = PaddingValues(
        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
        bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
                - WindowInsets.systemGestures.asPaddingValues().calculateBottomPadding()
    )