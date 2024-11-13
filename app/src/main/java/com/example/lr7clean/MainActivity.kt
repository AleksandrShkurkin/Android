package com.example.lr7clean

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room.databaseBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = databaseBuilder(applicationContext, AppDatabase::class.java, "database-name").build()
        val dao = db.flatAndResidentDao()

        val flat1 = Flat(flatNumber = 1, flatSize = "small")
        val flat2 = Flat(flatNumber = 2, flatSize = "medium")
        val flat3 = Flat(flatNumber = 3, flatSize = "large")

        var scopeIO = CoroutineScope(Job() + Dispatchers.IO)

        val job = scopeIO.launch {
            db.clearAllTables()
            dao.insertFlats(flat1)
            dao.insertFlats(flat2)
            dao.insertFlats(flat3)

            dao.insertResidents(
                Resident(residentName = "John", residentAge = 20, flatNumber = 1),
                Resident(residentName = "Bill", residentAge = 34, flatNumber = 3)
            )

            val residents = dao.getResident()
            val flats = dao.getFlat()
            val flatAndResident = dao.getFlatAndResident()
            val flatSizes = dao.getFlatSize()
            val residentNames = dao.getResidentName()

            with(Dispatchers.Main) {
                println(residents)
                println(flats)
                println(flatAndResident)
                println(flatSizes)
                println(residentNames)
            }
        }
    }
}