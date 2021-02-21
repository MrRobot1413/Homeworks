package ru.mrrobot1413.lesson8homework.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "movies")
@Parcelize
data class Movie(
    @PrimaryKey @SerializedName("id") val id: Long,
    @ColumnInfo(name = "title") @SerializedName("title") val title: String,
    @ColumnInfo(name = "overview") @SerializedName("overview") val overview: String,
    @ColumnInfo(name = "posterPath") @SerializedName("poster_path") val posterPath: String?,
    @ColumnInfo(name = "rating") @SerializedName("vote_average") val rating: Float,
    @ColumnInfo(name = "release_date") @SerializedName("release_date") val releaseDate: String,
    @ColumnInfo(name = "original_language") @SerializedName("original_language") val language: String
) :
    Parcelable {
    @ColumnInfo(name = "isLiked")
    @IgnoredOnParcel
    var liked = false
}
