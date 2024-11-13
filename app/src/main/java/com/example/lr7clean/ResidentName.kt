package com.example.lr7clean
import androidx.room.ColumnInfo

data class ResidentName(
    @ColumnInfo(name = "resident_name") var residentName: String?
)
