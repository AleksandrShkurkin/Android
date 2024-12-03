package com.example.lr9

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AppNavigation() {
    var useViewModel by remember { mutableStateOf(true) }

    Column {
        Switch(
            checked = useViewModel,
            onCheckedChange = { useViewModel = it }
        )
        if (useViewModel) {
            TaskScreenWithViewModel()
        } else {
            TaskScreenWithoutViewModel()
        }
    }
}