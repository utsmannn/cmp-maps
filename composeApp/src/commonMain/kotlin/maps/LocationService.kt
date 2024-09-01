package maps

import androidx.compose.runtime.Composable
import entity.data.Coordinate
import kotlinx.coroutines.flow.StateFlow

interface LocationService {
    val myLocation: StateFlow<Coordinate>

    suspend fun getMyLocation()
}

@Composable
expect fun rememberLocationService(): LocationService