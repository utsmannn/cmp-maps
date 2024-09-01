package maps

import androidx.compose.foundation.layout.PaddingValues

data class MapsSettings(
    val myLocationEnable: Boolean = false,
    val myLocationButtonEnabled: Boolean = false,
    val compassEnabled: Boolean = false,
    val padding: PaddingValues = PaddingValues()
)