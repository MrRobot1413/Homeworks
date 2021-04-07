package ru.mrrobot1413.movieapp.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mrrobot1413.movieapp.NotifyWorker
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    private var dbRepository: DbListRepository = DbListRepository.getInstance()
    private val _movies = MutableLiveData<List<Movie>>()
    val movies : LiveData<List<Movie>> = _movies
    private val _error = MutableLiveData<String>()
    val error : LiveData<String> = _error
    private val _movieDetailed = MutableLiveData<Movie>()
    val movieDetailed : LiveData<Movie> = _movieDetailed

    companion object{
        const val WORK_TAG = "notificationTag"
    }

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
                        _movies.postValue(responseBody.moviesList)
                        val list = ArrayList<Movie>()
                        if (_movies.value?.iterator()?.hasNext() == true) {
                            val next = _movies.value?.iterator()!!.next()
                            if (!next.liked) {
                                list.add(next)
                            }
                        }
                        saveAll(list)
                    } else {
                        _error.postValue("Error loading movies")
                    }
                } else {
                    _error.postValue("Error loading movies")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                _error.postValue("No connection")
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
                        _movies.postValue(responseBody.moviesList)
                    } else {
                        _error.postValue("Error loading movies")
                    }
                } else {
                    _error.postValue("Error loading movies")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                _error.postValue("No connection")
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
                        _movieDetailed.postValue(
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
                        Log.d("MOVIEZ_", _movieDetailed.toString())
                    } else {
                        _movieDetailed.postValue(
                            movie
                        )
                    }
                } else {
                    _movieDetailed.postValue(
                        movie
                    )
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                if (movie != null) {
                    _movieDetailed.postValue(
                        movie
                    )
                } else {
                    _error.postValue("No connection")
                }
            }
        })
    }

    fun scheduleNotification(
        movieName: String,
        scheduledTime: Calendar,
        context: Context,
        id: Int
    ) {
        val data = Data.Builder()
        data.putString(NotifyWorker.NAME, context.getString(R.string.watch_later_invite))
        data.putString(NotifyWorker.BODY, context.getString(R.string.watch_the_movie) + " \"$movieName\"")
        data.putInt(NotifyWorker.ICON, R.drawable.ic_baseline_movie_24)
        data.putInt(NotifyWorker.ID, id)
        data.putInt(NotifyWorker.GRAPH, R.navigation.nav_graph)
        data.putInt(NotifyWorker.DESTINATION, R.id.detailsFragment)

        val currentTime = Calendar.getInstance()

        val workRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .addTag(WORK_TAG)
            .setInputData(data.build())
            .setInitialDelay(scheduledTime.timeInMillis - currentTime.timeInMillis,
                TimeUnit.MILLISECONDS)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }

    fun saveAll(
        movies: List<Movie>,
    ) {
        dbRepository.saveAll(movies)
    }

    fun selectAll(): List<Movie> {
        return dbRepository.selectAll()
    }

    fun searchMovie(
        page: Int,
        query: String,
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
                            if (responseBody.moviesList.isEmpty()) {
                                _error.postValue("Nothing was found")
                            } else {
                                _movies.postValue(responseBody.moviesList)
                            }
                        } else {
                            _error.postValue("Error loading movies")
                        }
                    } else {
                        _error.postValue("Error loading movies")
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    _error.postValue("No connection")
                }
            })
    }
}