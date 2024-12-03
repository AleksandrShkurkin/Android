package com.example.lr9

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskCounter(tasks: List<Task>) {
    val completedTasks = tasks.count { it.isChecked }
    Text(
        text = "Completed tasks: $completedTasks/${tasks.size}",
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(8.dp)
    )
}