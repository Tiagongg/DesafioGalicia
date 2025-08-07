package com.example.desafiogalicia.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.desafiogalicia.data.local.dao.FavoriteUserDao
import com.example.desafiogalicia.data.local.entity.FavoriteUser

@Database(
    entities = [FavoriteUser::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun favoriteUserDao(): FavoriteUserDao
    
    companion object {
        const val DATABASE_NAME = "desafio_galicia_db"
    }
}