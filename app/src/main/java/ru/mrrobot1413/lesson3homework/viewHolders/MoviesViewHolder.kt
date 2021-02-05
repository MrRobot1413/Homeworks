package ru.mrrobot1413.lesson3homework.viewHolders

import android.annotation.SuppressLint
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sackcentury.shinebuttonlib.ShineButton
import ru.mrrobot1413.lesson3homework.model.Movie
import ru.mrrobot1413.lesson3homework.R

class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtName: TextView = itemView.findViewById(R.id.txt_name)
    var txtTime: TextView = itemView.findViewById(R.id.txt_time)
    var addToFavorToggle: ShineButton = itemView.findViewById(R.id.add_to_favor_toggle)
    var image: ImageView = itemView.findViewById(R.id.image_main)
    var btnDetails: Button = itemView.findViewById(R.id.btn_details)
    var txtRating: TextView = itemView.findViewById(R.id.txt_rating)

    @SuppressLint("SetTextI18n")
    fun bind(movie: Movie) {
        val activity = itemView.context
        txtName.text = activity.getString(movie.movieName)
        txtTime.text = activity.getString(movie.movieTime)
        image.setImageDrawable(movie.movieImage.let {
            ContextCompat.getDrawable(
                itemView.context,
                it
            )
        })
        txtRating.text = activity.getString(movie.movieRating)
    }

}