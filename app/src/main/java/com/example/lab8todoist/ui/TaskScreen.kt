package com.example.lab8todoist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab8todoist.data.Task
import com.example.lab8todoist.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val tasks by viewModel.tasks.collectAsState()
    var newTaskDescription by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("media") }
    var selectedCategory by remember { mutableStateOf("") }
    var showCompletedTasks by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var editTaskDescription by remember { mutableStateOf("") }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Mis Tareas", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        Button(onClick = {
            if (newTaskDescription.isNotBlank()) {
                viewModel.addTask(newTaskDescription, selectedPriority, selectedCategory)
                newTaskDescription = ""
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Agregar tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                TaskItem(task = task, onEditClick = {
                    taskToEdit = task
                    editTaskDescription = task.description
                    showEditDialog = true
                }, onDeleteClick = {
                    viewModel.deleteTask(task)
                })
            }
        }

        if (showEditDialog && taskToEdit != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                confirmButton = {
                    Button(onClick = {
                        taskToEdit?.let {
                            val updatedTask = it.copy(description = editTaskDescription)
                            viewModel.updateTask(updatedTask)
                        }
                        showEditDialog = false
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showEditDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Editar tarea") },
                text = {
                    TextField(
                        value = editTaskDescription,
                        onValueChange = { editTaskDescription = it },
                        label = { Text("Descripción de la tarea") }
                    )
                }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = task.description)
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
