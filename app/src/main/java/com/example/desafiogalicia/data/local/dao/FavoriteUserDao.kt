package com.example.desafiogalicia.data.local.dao

import androidx.room.*
import com.example.desafiogalicia.data.local.entity.FavoriteUser
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteUserDao {
    
    @Query("SELECT * FROM favorite_users")
    fun getAllFavorites(): Flow<List<FavoriteUser>>
    
    @Query("SELECT * FROM favorite_users WHERE uuid = :uuid")
    suspend fun getFavoriteByUuid(uuid: String): FavoriteUser?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteUser: FavoriteUser)
    
    @Delete
    suspend fun deleteFavorite(favoriteUser: FavoriteUser)
    
    @Query("DELETE FROM favorite_users WHERE uuid = :uuid")
    suspend fun deleteFavoriteByUuid(uuid: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_users WHERE uuid = :uuid)")
    suspend fun isFavorite(uuid: String): Boolean
}