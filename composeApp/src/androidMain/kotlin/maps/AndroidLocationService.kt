package maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import com.google.android.gms.location.FusedLocationProviderClient
import entity.data.Coordinate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

typealias GMSLocationService = com.google.android.gms.location.LocationServices

class AndroidLocationService(
    private val context: Context
) : LocationService {
    private val _myLocation: MutableStateFlow<Coordinate> = MutableStateFlow(Coordinate())
    override val myLocation: StateFlow<Coordinate>
        get() = _myLocation

    private var isGranted = false

    private val permission = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>


    private val fusedLocationProviderClient = GMSLocationService
        .getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getMyLocation() {
        if (isGranted) {
            // get location
            getLastLocation()
            println("permission granted!")
        } else {
            startRequestPermission()
        }
    }

    fun requestPermission() {
        if (!isGranted) {
            startRequestPermission()
        }
    }

    private fun startRequestPermission() {
        launcher.launch(permission)
    }

    fun setLauncher(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>) {
        this.launcher = launcher
    }

    fun setPermissionResult(granted: Boolean) {
        isGranted = granted
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isGranted) {
            fusedLocationProviderClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.exception == null) {
                        val location = task.result
                        val coordinate = Coordinate(
                            location.latitude,
                            location.longitude
                        )
                        _myLocation.update { coordinate }
                    }
                }
        }
    }
}