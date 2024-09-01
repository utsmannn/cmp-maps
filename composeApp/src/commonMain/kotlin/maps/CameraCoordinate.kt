package maps

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import entity.data.Coordinate

@Parcelize
data class CameraCoordinate(
    val coordinate: Coordinate = Coordinate(),
    val zoom: Float? = null,
    val initializer: Int? = null
) : Parcelable {

    fun zoomWithDefault(): Float {
        return zoom ?: 16f
    }
}

fun CameraCoordinate.isZeroCoordinate(): Boolean {
    return coordinate.latitude == 0.0 && coordinate.longitude == 0.0
}