package ru.mrrobot1413.lesson8homework.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
//todo некорректная модель.
/*
не бывает так, чтобы в модели были ссылки на ресурсы приложения
чаще всего модель приходит с String данными (database/network)
и эти данные уже отображаются
------------------
но если это чисто для эксперимента - то с натяжкой пойдет
 */
class Movie(
    val movieName: Int,
    val movieTime: Int,
    val movieImage: Int,
    val movieRating: Int,
    val movieActors: Int,
    val movieDescr: Int,
    val movieInviteText: Int//todo избыточное поле. Заменяется 1 паттерном на все фильмы, с изменением имени фильма
) :
    Parcelable{
        @IgnoredOnParcel
        var liked = false
    }
