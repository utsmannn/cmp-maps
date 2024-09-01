package entity.data

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
data class Coordinate(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable {

    override fun toString(): String {
        return "$latitude,$longitude"
    }
}