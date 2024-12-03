package com.example.lr9

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskBar(onAddTask: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("Task Details") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        Button(
            onClick = {
                if (title.isNotBlank() && details.isNotBlank()) {
                    onAddTask(title, details)
                    title = ""
                    details = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Task")
        }
    }
}