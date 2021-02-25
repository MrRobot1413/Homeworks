package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.*
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder


class MoviesAdapter(
    private var movies: MutableList<Movie>,
    private val clickListener: (movie: Movie) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    fun appendMovies(movies: List<Movie>) {
        this.movies.addAll(movies)
        notifyItemRangeInserted(
            this.movies.size,
            movies.size - 1
        )
    }

    override fun getItemCount() = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MoviesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {

        holder.bind(movies[position])
        setOnDetailsClickListener(holder, movies[position])
    }

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: Movie) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()

            clickListener(movie)
        }
    }
}