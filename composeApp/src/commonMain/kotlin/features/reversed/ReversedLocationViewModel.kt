package features.reversed

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.State
import entity.data.Coordinate
import entity.data.Place
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import repository.LocationRepository

data class ReversedLocationModel(
    val coordinate: Coordinate = Coordinate(),
    val placeState: State<List<Place>> = State.Idle,
    val isReversedButtonEnable: Boolean = false
)

sealed class ReversedLocationIntent {
    data class Latitude(val lat: Double) : ReversedLocationIntent()
    data class Longitude(val long: Double) : ReversedLocationIntent()
    data object GetPlaces : ReversedLocationIntent()
}

class ReversedLocationViewModel : BaseViewModel<ReversedLocationModel, ReversedLocationIntent>(
    ReversedLocationModel()
) {
    private val locationRepository = LocationRepository()

    override fun handleIntent(appIntent: ReversedLocationIntent) {
        when (appIntent) {
            is ReversedLocationIntent.Latitude -> {
                setLatitude(appIntent.lat)
            }
            is ReversedLocationIntent.Longitude -> {
                setLongitude(appIntent.long)
            }
            is ReversedLocationIntent.GetPlaces -> {
                reversedLocation()
            }
        }
    }

    private fun reversedLocation() = viewModelScope.launch {
        val coordinate = stateModel.value.coordinate
        locationRepository.reverseLocation(
            coordinate
        ).stateIn(this)
            .collectLatest {
                updateModel { model ->
                    model.copy(
                        placeState = it
                    )
                }
            }
    }

    private fun setLongitude(long: Double) {
        updateModel { model ->
            val coordinate = model.coordinate
            model.copy(
                coordinate = coordinate.copy(
                    longitude = long
                ),
                isReversedButtonEnable = setButtonEnable()
            )
        }
    }

    private fun setLatitude(lat: Double) {
        updateModel { model ->
            val coordinate = model.coordinate
            model.copy(
                coordinate = coordinate.copy(
                    latitude = lat
                ),
                isReversedButtonEnable = setButtonEnable()
            )
        }
    }

    private fun setButtonEnable(): Boolean {
        val coordinate = stateModel.value.coordinate
        val isLatitudeValid = coordinate.latitude.toString().isNotEmpty()
        val isLongitudeValid = coordinate.longitude.toString().isNotEmpty()

        return isLatitudeValid && isLongitudeValid
    }
}