package com.example.lr9

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TaskItem(
    task: Task,
    onCheckComplete: () -> Unit,
    onTaskDelete: (Task) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var isExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { isExpanded = !isExpanded },
            elevation = 4.dp,
            backgroundColor = if (task.isChecked) Color(0xFFE0E0E0) else Color.White,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = task.isChecked,
                        onCheckedChange = { onCheckComplete() }
                    )
                    Text(
                        text = task.title,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                    TextButton(
                        onClick = {
                            isVisible = false
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                ) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    if (!isVisible) {
        LaunchedEffect(Unit) {
            delay(300)
            onTaskDelete(task)
        }
    }
}