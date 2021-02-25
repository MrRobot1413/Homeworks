package ru.mrrobot1413.lesson8homework.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val moviesList: List<Movie>,
    @SerializedName("total_pages")
    val totalPages: Int
)

