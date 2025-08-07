package com.example.desafiogalicia.data.repository

import com.example.desafiogalicia.data.local.dao.FavoriteUserDao
import com.example.desafiogalicia.data.local.entity.FavoriteUser
import com.example.desafiogalicia.data.model.User
import com.example.desafiogalicia.data.remote.RandomUserApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val randomUserApi: RandomUserApi,
    private val favoriteUserDao: FavoriteUserDao
) {
    
    suspend fun getUsers(
        page: Int = 1,
        results: Int = 10,
        nationality: String? = null
    ): Result<List<User>> {
        return try {
            // Generar seed único por nacionalidad para evitar solapamiento de UUIDs
            val seed = if (nationality.isNullOrBlank()) {
                "challenge"
            } else {
                "challenge-$nationality"
            }
            
            val response = randomUserApi.getUsers(
                results = results,
                page = page,
                nationality = nationality,
                seed = seed
            )
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Métodos de usuarios favoritos
    fun getAllFavorites(): Flow<List<FavoriteUser>> {
        return favoriteUserDao.getAllFavorites()
    }
    
    suspend fun addToFavorites(user: User) {
        val favoriteUser = FavoriteUser(
            uuid = user.login.uuid,
            fullName = user.name.fullName,
            email = user.email,
            country = user.location.country,
            pictureUrl = user.picture.large
        )
        favoriteUserDao.insertFavorite(favoriteUser)
    }
    
    suspend fun removeFromFavorites(uuid: String) {
        favoriteUserDao.deleteFavoriteByUuid(uuid)
    }
    
    suspend fun isFavorite(uuid: String): Boolean {
        return favoriteUserDao.isFavorite(uuid)
    }
}