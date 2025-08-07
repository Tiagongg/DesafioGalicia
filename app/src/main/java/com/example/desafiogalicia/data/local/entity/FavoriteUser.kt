package com.example.desafiogalicia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_users")
data class FavoriteUser(
    @PrimaryKey
    val uuid: String,
    val fullName: String,
    val email: String,
    val country: String,
    val pictureUrl: String
)