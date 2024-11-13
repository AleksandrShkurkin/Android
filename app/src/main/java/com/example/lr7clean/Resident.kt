package com.example.lr7clean
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Resident(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @ColumnInfo(name = "resident_name") var residentName: String?,
    @ColumnInfo(name = "resident_age") var residentAge: Int,
    @ColumnInfo(name = "occupied_flat_number") var flatNumber: Int
)
