package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.MovieResponse
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.repositories.DbListRepository
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    private var dbRepository: DbListRepository = DbListRepository.getInstance()
    var movies = MutableLiveData<List<Movie>>()
    var error = MutableLiveData<String>()
    var movieDetailed = MutableLiveData<Movie>()

    fun getPopularMovies(
        page: Int,
    ) {
        movieRepository.getPopularMovies(page = page).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        movies.postValue(responseBody.moviesList)
                        val list = ArrayList<Movie>()
                        if (movies.value?.iterator()?.hasNext() == true){
                            val next = movies.value?.iterator()!!.next()
                            if(!next.liked){
                                list.add(next)
                            }
                        }
                        saveAll(list)
                    } else {
                        error.postValue("Error loading movies")
                    }
                } else {
                    error.postValue("Error loading movies")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                error.postValue("No connection")
            }
        })
    }

    fun getTopRatedMovies(
        page: Int,
    ) {
        movieRepository.getTopRatedMovies(page).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        movies.postValue(responseBody.moviesList)
                    } else {
                        error.postValue("Error loading movies")
                    }
                } else {
                    error.postValue("Error loading movies")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                error.postValue("No connection")
            }
        })
    }

    fun getMovieDetails(
        id: Int,
    ) {
        movieRepository.getMovieDetails(id = id).enqueue(object : Callback<Movie> {
            val repository = DbListRepository.getInstance()
            val movie = repository.selectById(id)

            override fun onResponse(
                call: Call<Movie>,
                response: Response<Movie>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        movieDetailed.postValue(
                            Movie(
                                responseBody.id,
                                responseBody.title,
                                responseBody.overview,
                                responseBody.posterPath,
                                responseBody.rating,
                                responseBody.releaseDate,
                                responseBody.time,
                                responseBody.language
                            )
                        )
                    } else {
                        movieDetailed.postValue(
                            movie
                        )
                    }
                } else {
                    movieDetailed.postValue(
                        movie
                    )
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                if (movie != null) {
                    movieDetailed.postValue(
                        movie
                    )
                } else {
                    error.postValue("No connection")
                }
            }
        })
    }

    fun saveAll(
        movies: List<Movie>
    ){
        dbRepository.saveAll(movies)
    }

    fun selectAll(): List<Movie>{
        return dbRepository.selectAll()
    }

    fun searchMovie(
        page: Int,
        query: String
    ) {
        movieRepository.searchMovie(page = page, query = query)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            if (responseBody.moviesList.isEmpty()){
                                error.postValue("Nothing was found")
                            } else{
                                movies.postValue(responseBody.moviesList)
                            }
                        } else {
                            error.postValue("Error loading movies")
                        }
                    } else {
                        error.postValue("Error loading movies")
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    error.postValue("No connection")
                }
            })
    }

    fun getSeries(
        page: Int = 1,
        onSuccess: ((movies: List<Series>) -> Unit),
        onError: (() -> Unit),
    ) {
        movieRepository.getSeries(page, onSuccess, onError)
    }
}