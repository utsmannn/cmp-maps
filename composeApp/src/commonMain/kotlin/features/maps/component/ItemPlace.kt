package features.maps.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import entity.data.Place
import kotlincomposemultiplatform1.composeapp.generated.resources.Res
import kotlincomposemultiplatform1.composeapp.generated.resources.ic_pin_marker
import org.jetbrains.compose.resources.painterResource

@Composable
fun ItemPlace(
    place: Place,
    onClickPlace: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable {
                onClickPlace.invoke()
            }
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.width(34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_pin_marker),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = place.distanceOnKm(),
                style = TextStyle.Default
                    .copy(
                        color = Color.Gray,
                        fontSize = 10.sp
                    ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.width(6.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = place.name,
                style = TextStyle.Default
                    .copy(
                        fontWeight = FontWeight.SemiBold
                    )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = place.address,
                style = TextStyle.Default
                    .copy(
                        fontSize = 12.sp
                    )
            )
        }
    }
}