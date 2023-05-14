package com.roanderson.mockwebserver.data.model

data class FilmDetailDTO(
    val errorMessage: String,
    val expression: String,
    val results: List<Result>,
    val searchType: String
)