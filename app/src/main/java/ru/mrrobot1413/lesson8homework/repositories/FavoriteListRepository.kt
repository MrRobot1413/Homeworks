package ru.mrrobot1413.lesson8homework.repositories

import androidx.lifecycle.MutableLiveData
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.Movie

class FavoriteListRepository {
    private lateinit var instance: FavoriteListRepository
    private var dataSet: MutableList<Movie>? = DataStorage.favoriteList

    fun getInstance(): FavoriteListRepository {
        instance = FavoriteListRepository()
        return instance
    }

    fun getMovies(): MutableLiveData<List<Movie>> {
        val data: MutableLiveData<List<Movie>> = MutableLiveData()
        data.value = dataSet
        return data
    }
}