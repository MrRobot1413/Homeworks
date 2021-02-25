package ru.mrrobot1413.lesson8homework.interfaces

import ru.mrrobot1413.lesson8homework.model.Movie

interface OnGetMoviesCallback {
    fun onSuccess(movies: List<Movie?>?)

    fun onError()
}