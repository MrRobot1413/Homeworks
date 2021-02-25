package ru.mrrobot1413.lesson8homework.repositories

import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mrrobot1413.lesson8homework.api.Api
import ru.mrrobot1413.lesson8homework.model.Movie
//import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.MovieResponse

object MovieRepository {
    private lateinit var instance: MovieRepository

    //    private var dataSet: MutableList<Movie> = DataStorage.moviesList
    private val api: Api

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getInstance(): MovieRepository {
        instance = MovieRepository
        return instance
    }

    fun getMovies(
        page: Int = 1,
        onSuccess: ((movies: List<Movie>) -> Unit),
        onError: (() -> Unit)
    ) {
        api.getMovies(page = page)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.moviesList)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }
}
