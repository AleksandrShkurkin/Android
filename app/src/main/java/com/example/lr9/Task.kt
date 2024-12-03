package com.example.lr9

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    var isChecked: Boolean = false
)
