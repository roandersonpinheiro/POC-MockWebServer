package com.roanderson.mockwebserver.data.services

import com.roanderson.mockwebserver.data.model.GameModelDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GamesApiService {

    @GET("games")
    suspend fun getGames(): Response<List<GameModelDTO>>

    @GET("games")
    suspend fun getGameById(@Query("id") id: Int): Response<List<GameModelDTO>>
}