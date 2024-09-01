package entity

import entity.data.Coordinate
import entity.data.Place
import entity.response.PlaceResponse

object PlaceMapper {

    fun mapResponseToPlaces(response: PlaceResponse) : List<Place> {
        return response.items?.map {
            mapItemResponseToPlace(it)
        }.orEmpty()
    }

    private fun mapItemResponseToPlace(itemResponse: PlaceResponse.Item?): Place {
        return Place(
            id = itemResponse?.id.orEmpty(),
            name = itemResponse?.title.orEmpty(),
            address = itemResponse?.address?.let {
                "${it.label} - ${it.street}, ${it.city}"
            }.orEmpty(),
            coordinate = itemResponse?.position?.let {
                Coordinate(
                    latitude = it.lat ?: 0.0,
                    longitude = it.lng ?: 0.0
                )
            } ?: Coordinate(),
            distance = itemResponse?.distance ?: -1
        )
    }
}