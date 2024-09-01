package features.search

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.State
import entity.data.Coordinate
import entity.data.Place
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import repository.LocationRepository

data class SearchLocationModel(
    val query: String = "",
    val placeState: State<List<Place>> = State.Idle
)

sealed class SearchLocationIntent {
    data class Query(val query: String) : SearchLocationIntent()
    data object Search : SearchLocationIntent()
}

class SearchLocationViewModel : BaseViewModel<SearchLocationModel, SearchLocationIntent>(
    SearchLocationModel()
) {

    private val locationRepository = LocationRepository()

    override fun handleIntent(appIntent: SearchLocationIntent) {
        when (appIntent) {
            is SearchLocationIntent.Query -> {
                sendQuery(appIntent.query)
            }
            is SearchLocationIntent.Search -> {
                searchLocation()
            }
        }
    }

    private fun searchLocation() = viewModelScope.launch {
        val query = stateModel.value.query
        locationRepository.searchLocation(
            query = query,
            coordinate = Coordinate(
                latitude = -6.361380449431958,
                longitude = 106.8334773180715
            )
        ).stateIn(this)
            .collectLatest {
                updateModel { model ->
                    model.copy(
                        placeState = it
                    )
                }
            }
    }

    private fun sendQuery(query: String) {
        updateModel { model ->
            model.copy(
                query = query
            )
        }
    }
}