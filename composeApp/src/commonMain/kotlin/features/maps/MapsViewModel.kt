package features.maps

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.State
import entity.data.Coordinate
import entity.data.Place
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import maps.GoogleMapsMarker
import repository.LocationRepository

data class MapsState(
    val query: String = "",
    val placesState: State<List<Place>> = State.Idle,
    val selectedPlace: Place = Place.Empty,
    val isShowSearch: Boolean = false,
    val myCoordinate: Coordinate = Coordinate(),
    val mapBottomPadding: Dp = 0.dp,
    val showProfileDialog: Boolean = false
)

sealed class MapsIntent {
    data class SetQuery(val query: String) : MapsIntent()
    data class SetSelectedPlace(val place: Place) : MapsIntent()
    data class SetIsShowSearch(val isShowSearch: Boolean) : MapsIntent()
    data object ObserverQuery : MapsIntent()
    data class SetMyCoordinate(val coordinate: Coordinate) : MapsIntent()
    data class SetSelectedMarker(val marker: GoogleMapsMarker) : MapsIntent()
    data object SetPlacesClear : MapsIntent()
    data class SetBottomMapPadding(val padding: Dp) : MapsIntent()
    data object ShowProfileDialog : MapsIntent()
    data object DismissProfileDialog : MapsIntent()
}

class MapsViewModel : BaseViewModel<MapsState, MapsIntent>(
    MapsState()
) {

    private val locationRepository = LocationRepository()

    override fun handleIntent(appIntent: MapsIntent) {
        when (appIntent) {
            is MapsIntent.SetQuery -> {
                setQuerySearch(appIntent.query)
            }

            is MapsIntent.SetSelectedPlace -> {
                setSelectedPlace(appIntent.place)
            }

            is MapsIntent.SetIsShowSearch -> {
                setIsShowSearch(appIntent.isShowSearch)
            }

            is MapsIntent.ObserverQuery -> {
                observerQuery()
            }

            is MapsIntent.SetMyCoordinate -> {
                setMyCoordinate(appIntent.coordinate)
            }

            is MapsIntent.SetSelectedMarker -> {
                setSelectedMarker(appIntent.marker)
            }

            is MapsIntent.SetPlacesClear -> {
                restartPlacesState()
            }

            is MapsIntent.SetBottomMapPadding -> {
                setBottomMapPadding(appIntent.padding)
            }

            is MapsIntent.ShowProfileDialog -> {
                showProfileDialog(true)
            }

            is MapsIntent.DismissProfileDialog -> {
                showProfileDialog(false)
            }
        }
    }

    private fun showProfileDialog(show: Boolean) {
        updateModel {
            it.copy(
                showProfileDialog = show
            )
        }
    }

    private fun setBottomMapPadding(padding: Dp) {
        updateModel {
            it.copy(
                mapBottomPadding = padding
            )
        }
    }

    private fun setSelectedMarker(marker: GoogleMapsMarker) {
        val selectedPlace = stateModel.value.placesState
        if (selectedPlace is State.Success) {
            val places = selectedPlace.data
            val place = places.find { it.coordinate.toString() == marker.coordinate.toString() }

            if (place != null) {
                setSelectedPlace(place)
            }
        }
    }

    private fun setMyCoordinate(coordinate: Coordinate) {
        updateModel {
            it.copy(
                myCoordinate = coordinate
            )
        }
    }

    private fun observerQuery() = viewModelScope.launch {
        stateModel
            .map { it.query }
            .debounce(1000)
            .stateIn(this)
            .collectLatest {
                if (it.length > 2) {
                    searchPlace()
                }

                if (it.isEmpty()) {
                    restartPlacesState()
                }
            }
    }

    private fun setIsShowSearch(showSearch: Boolean) {
        updateModel {
            it.copy(
                isShowSearch = showSearch
            )
        }
    }

    private fun setSelectedPlace(place: Place) {
        println("places: set selected place -> $place")
        updateModel {
            it.copy(
                selectedPlace = place
            )
        }
    }

    private fun setQuerySearch(query: String) {
        updateModel {
            it.copy(
                query = query
            )
        }
    }

    private fun searchPlace() = viewModelScope.launch {
        val query = stateModel.value.query
        val coordinate = stateModel.value.myCoordinate
        locationRepository
            .searchLocation(query, coordinate)
            .stateIn(this)
            .collectLatest { placeState ->
                updateModel {
                    it.copy(
                        placesState = placeState
                    )
                }
            }
    }

    private fun restartPlacesState() {
        updateModel {
            MapsState().copy(
                myCoordinate = it.myCoordinate
            )
        }
    }
}