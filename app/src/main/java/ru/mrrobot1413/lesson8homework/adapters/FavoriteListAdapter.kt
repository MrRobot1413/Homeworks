package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.data.DataStorage

//import ru.mrrobot1413.lesson8homework.data.DataStorage

class FavoriteListAdapter(
    private val noMoviesSign: TextView,
    private val clickListener: (movie: Movie) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    private lateinit var moviesList: List<Movie>

    fun setMovies(moviesList: List<Movie>){
        this.moviesList = moviesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = moviesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MoviesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {

        holder.bind(moviesList[position])
        setOnDetailsClickListener(holder, moviesList[position])
        showNoMoviesSign()
    }

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: Movie) {
        holder.holder.setOnClickListener {

            notifyDataSetChanged()

            clickListener(movie)
        }
    }

    private fun showNoMoviesSign(){
        if(DataStorage.favoriteList.isEmpty()){
            noMoviesSign.visibility = View.VISIBLE
        } else{
            noMoviesSign.visibility = View.GONE
        }
    }
}