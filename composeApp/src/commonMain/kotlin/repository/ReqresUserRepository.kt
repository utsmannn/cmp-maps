package repository

import base.BaseRepository
import base.State
import entity.ReqresMapper
import entity.data.User
import entity.response.ReqresResponse
import kotlinx.coroutines.flow.Flow

class ReqresUserRepository : BaseRepository() {

    fun getUser(): Flow<State<User>> {
        return suspend {
            getHttpResponse("https://reqres.in/api/users?page=2")
        }.reduce<ReqresResponse, User> {
            val user = ReqresMapper.mapResponseToUser(it)
            State.Success(user)
        }
    }
}