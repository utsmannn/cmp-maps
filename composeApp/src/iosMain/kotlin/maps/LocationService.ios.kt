package maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberLocationService(): LocationService {
    return remember {
        IosLocationService()
    }
}