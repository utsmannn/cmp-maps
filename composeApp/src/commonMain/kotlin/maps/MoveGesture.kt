package maps

import entity.data.Coordinate

sealed class MoveGesture {
    data object MoveNotStarted : MoveGesture()
    data object MoveStart : MoveGesture()
    data class MoveStop(val coordinate: Coordinate) : MoveGesture()
}