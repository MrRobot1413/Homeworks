package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.data.DataStorage

class FavoriteAdapter(
    private val moviesList: List<Movie>,
    private val noMoviesSign: TextView,
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

        holder.bind(moviesList[position])
        setOnDetailsClickListener(holder, moviesList[position])
        setOnLikeListener(holder, moviesList[position], position)
        showNoMoviesSign()
        holder.addToFavorToggle.isChecked = true
    }

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: Movie) {
        holder.btnDetails.setOnClickListener {

            notifyDataSetChanged()

            clickListener(movie)
        }
    }

    private fun setOnLikeListener(
        holder: MoviesViewHolder,
        movie: Movie,
        position: Int
    ) {
        holder.addToFavorToggle.setOnClickListener {
            movie.liked = false
            DataStorage.favoriteList.removeAt(position)
            showNoMoviesSign()

            notifyItemRemoved(position)
            notifyItemChanged(position)//todo избыточно

            val context = holder.itemView.context
            //todo вынести из адаптера (это дублирующийся код)
            Snackbar.make(holder.itemView, "${context.getString(R.string.toast)} '${context.getString(movie.movieName)}' ${
                context.getString(
                    R.string.toast_delete
                )
            }", Snackbar.LENGTH_LONG).setAction(
                context.getString(R.string.undo)
            ) {
                movie.liked = true
                DataStorage.favoriteList.add(movie)
                notifyDataSetChanged()
            }.show()
        }
    }

    private fun showNoMoviesSign(){//todo выполняется во ViewModel, а не в адаптере
        if(DataStorage.favoriteList.isEmpty()){
            noMoviesSign.visibility = View.VISIBLE
        } else{
            noMoviesSign.visibility = View.GONE
        }
    }
}