package com.example.desafiogalicia.data.remote

import com.example.desafiogalicia.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    
    @GET(".")
    suspend fun getUsers(
        @Query("results") results: Int = 10,
        @Query("page") page: Int = 1,
        @Query("nat") nationality: String? = null,
        @Query("seed") seed: String
    ): UserResponse
}