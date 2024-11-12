package com.example.lr6

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity()
{
    private var groceries: ArrayList<Item> = ArrayList()
    private var buttons: Array<String> = arrayOf("Select", "ListView", "ScrollView")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setInitialData()

        val recyclerView = findViewById<RecyclerView>(R.id.main)
        val adapter = ItemAdapter(this, groceries)

        recyclerView.adapter = adapter

        val spinner = findViewById<Spinner>(R.id.actionSpinner)
        val adapterSpinner: ArrayAdapter<String?> = ArrayAdapter(this, android.R.layout.simple_spinner_item, buttons)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2)
                {
                    0 -> {}
                    1 -> {
                        startActivity(Intent(this@MainActivity, ListViewActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this@MainActivity, ScrollViewActivity::class.java))
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setInitialData()
    {
        groceries.add(Item("Milk", 40))
        groceries.add(Item("Bread", 7))
        groceries.add(Item("Eggs", 25))
    }
}