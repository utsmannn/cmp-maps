package features.maps.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import entity.data.Place

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselPlaces(
    modifier: Modifier,
    places: List<Place>,
    selectedPlace: Place,
    onPageChange: (Place) -> Unit
) {

    if (places.isNotEmpty()) {
        val selectedIndex = remember(selectedPlace) {
            val index = places.indexOf(selectedPlace)
            if (index < 0) {
                0
            } else {
                index
            }
        }

        val pagerState = rememberPagerState(selectedIndex) { places.size }

        LaunchedEffect(pagerState.currentPage) {
            onPageChange.invoke(places[pagerState.currentPage])
        }

        LaunchedEffect(selectedIndex) {
            pagerState.scrollToPage(selectedIndex)
        }

        HorizontalPager(
            modifier = Modifier.fillMaxWidth().then(modifier),
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp)
        ) { index ->

            val place = remember(index) {
                places[index]
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(CarouselHeight)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {

                ItemPlace(place) {

                }
            }
        }
    }
}

val CarouselHeight = 100.dp