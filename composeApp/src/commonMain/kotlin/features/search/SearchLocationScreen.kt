package features.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import entity.data.Place
import onFailure
import onLoading
import onSuccess

@Composable
fun SearchLocationScreen(
    viewModel: SearchLocationViewModel = viewModel { SearchLocationViewModel() }
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
                value = model.query,
                onValueChange = {
                    viewModel.handleIntent(
                        SearchLocationIntent.Query(it)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
                    viewModel.handleIntent(
                        SearchLocationIntent.Search
                    )
                },
                enabled = model.query.isNotEmpty()
            ) {
                Text("Seach")
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

@Composable
fun PlaceContent(place: Place) {
    Card {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = place.name,
                style = TextStyle.Default
                    .copy(
                        fontWeight = FontWeight.Bold
                    )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = place.address,
                style = TextStyle.Default
            )

            Spacer(Modifier.height(6.dp))
            Text(
                text = place.coordinate.toString(),
                style = TextStyle.Default
            )
        }
    }
}