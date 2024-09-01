package maps

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberLocationService(): LocationService {
    val androidContext = LocalContext.current
    val androidLocationService = remember {
        AndroidLocationService(androidContext)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val isGranted = !result.map { it.value }.contains(false)
        println("permission granted: $isGranted")
        androidLocationService.setPermissionResult(isGranted)
    }

    LaunchedEffect(launcher) {
        androidLocationService.setLauncher(launcher)
    }

    LaunchedEffect(Unit) {
        androidLocationService.requestPermission()
    }

    return androidLocationService
}