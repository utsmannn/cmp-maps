package maps

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import entity.data.Coordinate

@Parcelize
data class GoogleMapsMarker(
    val coordinate: Coordinate,
    val title: String? = null
) : Parcelable