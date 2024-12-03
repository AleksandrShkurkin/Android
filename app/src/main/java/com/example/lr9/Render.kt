package com.example.lr9

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun RefreshScreen(content: @Composable () -> Unit) {
    var key by remember { mutableIntStateOf(0) }
    Column {
        Button(onClick = { key++ }) {
            Text("Refresh Screen")
        }
        key(key) {
            content()
        }
    }
}

@Composable
fun MainScreen() {
    RefreshScreen {
        AppNavigation()
    }
}