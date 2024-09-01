package features.reversed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import features.search.PlaceContent
import features.search.SearchLocationIntent
import onFailure
import onLoading
import onSuccess

@Composable
fun ReversedLocationScreen(
    viewModel: ReversedLocationViewModel = viewModel { ReversedLocationViewModel() }
) {

    val model by viewModel.stateModel.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = model.coordinate.latitude.toString(),
                onValueChange = {
                    viewModel.handleIntent(
                        ReversedLocationIntent.Latitude(it.toDoubleOrNull() ?: 0.0)
                    )
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("latitude")
                }
            )

            Spacer(Modifier.width(12.dp))

            TextField(
                value = model.coordinate.longitude.toString(),
                onValueChange = {
                    viewModel.handleIntent(
                        ReversedLocationIntent.Longitude(it.toDoubleOrNull() ?: 0.0)
                    )
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("longitude")
                }
            )

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
                    viewModel.handleIntent(
                        ReversedLocationIntent.GetPlaces
                    )
                },
                enabled = model.isReversedButtonEnable
            ) {
                Text("get places")
            }
        }

        Spacer(Modifier.height(12.dp))

        with(model.placeState) {
            onLoading {
                CircularProgressIndicator()
            }
            onSuccess { places ->
                for (place in places) {
                    PlaceContent(place)
                    Spacer(Modifier.height(12.dp))
                }
            }
            onFailure {
                Text(it.message.orEmpty())
            }
        }
    }
}