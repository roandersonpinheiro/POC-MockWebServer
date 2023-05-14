package com.roanderson.mockwebserver.data.services

import com.roanderson.mockwebserver.data.model.FilmDetailDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FilmIMDbApiService {

    @GET("k_tkkbokr8/{name}")
    suspend fun searchMovie(@Path("name") name: String): Response<FilmDetailDTO>
}