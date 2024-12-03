package com.example.lr9

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TaskScreenWithViewModel(viewModel: TaskViewModel = viewModel()) {
    val tasks = viewModel.tasks
    Column {
        AddTaskBar(onAddTask = { title, details ->
            viewModel.addTask(title, details)
        })
        TaskCounter(tasks)
        TaskList(
            tasks = tasks,
            onTaskCheck = viewModel::checkTask,
            onTaskDelete = viewModel::deleteTask
        )
    }
}