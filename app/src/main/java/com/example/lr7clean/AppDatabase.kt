package com.example.lr7clean

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Flat::class, Resident::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flatAndResidentDao(): FlatResidentDao
}