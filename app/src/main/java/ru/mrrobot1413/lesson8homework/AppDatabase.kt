package ru.mrrobot1413.lesson8homework

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series

@Database(entities = [Movie::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}