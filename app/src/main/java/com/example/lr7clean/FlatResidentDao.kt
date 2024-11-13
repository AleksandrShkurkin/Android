package com.example.lr7clean
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface FlatResidentDao {
    @Insert
    fun insertFlats(vararg flat: Flat)

    @Insert
    fun insertResidents(vararg resident: Resident)

    @Update
    fun updateFlats(vararg flat: Flat)

    @Update
    fun updateResidents(vararg resident: Resident)

    @Delete
    fun deleteFlats(vararg flat: Flat)

    @Delete
    fun deleteResidents(vararg resident: Resident)

    @Query("SELECT * FROM resident")
    fun getResident(): List<Resident>

    @Query("SELECT * FROM flat")
    fun getFlat(): List<Flat>

    @Query("SELECT resident_name FROM resident")
    fun getResidentName(): List<ResidentName>

    @Query("SELECT flat_size FROM flat")
    fun getFlatSize(): List<FlatSize>

    @Transaction
    @Query("SELECT * FROM flat")
    fun getFlatAndResident(): List<FlatAndResident>
}