package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.mrrobot1413.lesson8homework.*
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder

class MoviesAdapter(
    private val moviesList: List<Movie>,
    private val clickListener: (movie: Movie) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    override fun getItemCount() = moviesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MoviesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        //todo можно вынести получение элемента, и потом его передать, а не брать каждый раз из List
        holder.bind(moviesList[position])
        setOnDetailsClickListener(holder, moviesList[position])
        setOnLikeListener(holder, moviesList[position])

    }

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: Movie) {
        holder.btnDetails.setOnClickListener {

            notifyDataSetChanged()

            clickListener(movie)
        }
    }


    private fun setOnLikeListener(
        holder: MoviesViewHolder,
        movie: Movie
    ) {
        holder.addToFavorToggle.isChecked = movie.liked
        holder.addToFavorToggle.setOnClickListener {
            if (movie.liked) {
                movie.liked = false//todo убрать дублирование
                DataStorage.favoriteList.remove(movie)
                notifyDataSetChanged()//todo убрать дублирование
            } else {
                movie.liked = true
                DataStorage.favoriteList.add(movie)
                notifyDataSetChanged()

                val context = holder.itemView.context

                //todo вынести отображение в Activity
                Snackbar.make(holder.itemView, "${context.getString(R.string.toast)} '${context.getString(movie.movieName)}' ${
                    context.getString(
                        R.string.toast_add
                    )
                }", Snackbar.LENGTH_LONG).setAction(
                    context.getString(R.string.undo)
                ) {
                    movie.liked = false
                    DataStorage.favoriteList.remove(movie)
                    notifyDataSetChanged()
                }.show()
            }
        }
    }
}