package com.example.lr9

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
fun TaskScreenWithoutViewModel() {
    val tasks = remember { mutableStateListOf<Task>() }

    if (tasks.isEmpty()) {
        tasks.addAll(
            listOf(
                Task(1, "Read a book", "Finish 'Compose for Android'"),
                Task(2, "Cook dinner", "Try a new recipe")
            )
        )
    }

    Column {
        AddTaskBar(onAddTask = { title, details ->
            val id = if (tasks.isEmpty()) 1 else tasks.maxOf { it.id } + 1
            tasks.add(Task(id, title, details))
        })
        TaskCounter(tasks)
        TaskList(
            tasks = tasks,
            onTaskCheck = { task ->
                val index = tasks.indexOf(task)
                if (index >= 0) {
                    tasks[index] = task.copy(isChecked = !task.isChecked)
                }
            },
            onTaskDelete = { tasks.remove(it) }
        )
    }
}