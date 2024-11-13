package com.example.lr7clean
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Flat (
    @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @ColumnInfo(name = "flat_number") var flatNumber: Int,
    @ColumnInfo(name = "flat_size") var flatSize: String?
)
