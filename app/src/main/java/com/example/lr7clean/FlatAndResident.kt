package com.example.lr7clean
import androidx.room.Embedded
import androidx.room.Relation

data class FlatAndResident(
    @Embedded val flat: Flat,
    @Relation(
        parentColumn = "flat_number",
        entityColumn = "occupied_flat_number"
    ) val resident: Resident?
)
