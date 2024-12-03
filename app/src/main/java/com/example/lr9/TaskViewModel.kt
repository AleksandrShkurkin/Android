package com.example.lr9

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    private val taskMutable = mutableStateListOf<Task>()
    val tasks: List<Task> = taskMutable

    init {
        taskMutable.addAll(
            listOf(
                Task(1, "Test1", "Test1text"),
                Task(2, "Test2", "Test2text"),
                Task(3, "Test3", "Test3text"),
            )
        )
    }

    fun addTask(title: String, description: String) {
        val id = if (taskMutable.isEmpty()) 1 else taskMutable.maxOf { it.id } + 1
        taskMutable.add(Task(id = id, title = title, description = description))
    }

    fun checkTask(task: Task)
    {
        val index = taskMutable.indexOf(task)
        if (index >= 0)
        {
            taskMutable[index] = task.copy(isChecked = !task.isChecked)
        }
    }

    fun deleteTask(task: Task)
    {
        taskMutable.remove(task)
    }

}