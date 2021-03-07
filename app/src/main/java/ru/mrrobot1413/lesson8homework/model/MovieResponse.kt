package ru.mrrobot1413.lesson8homework.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("pages")
    val page: Int,
    @SerializedName("results")
    val moviesList: List<MovieDetailed>,
    @SerializedName("total_pages")
    val totalPages: Int
)

