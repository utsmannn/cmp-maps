package repository

import base.BaseRepository
import base.State
import entity.PlaceMapper
import entity.data.Coordinate
import entity.data.Place
import entity.response.PlaceResponse
import kotlinx.coroutines.flow.Flow
import org.utsman.cmpbasic.SecretConfig

class LocationRepository : BaseRepository() {

    fun searchLocation(query: String, coordinate: Coordinate): Flow<State<List<Place>>> {
        val hereApiKey = SecretConfig.HERE_API_KEY
        return suspend {
            getHttpResponse("https://discover.search.hereapi.com/v1/discover?at=$coordinate&limit=20&q=$query&apiKey=$hereApiKey")
        }.reduce<PlaceResponse, List<Place>> { response ->
            val data = PlaceMapper.mapResponseToPlaces(response)
            State.Success(data)
        }
    }

    fun reverseLocation(coordinate: Coordinate): Flow<State<List<Place>>> {
        val hereApiKey = SecretConfig.HERE_API_KEY
        return suspend {
            getHttpResponse("https://revgeocode.search.hereapi.com/v1/revgeocode?at=$coordinate&limit=3&lang=en-US&apiKey=$hereApiKey")
        }.reduce<PlaceResponse, List<Place>> { response ->
            val data = PlaceMapper.mapResponseToPlaces(response)
            State.Success(data)
        }
    }
}