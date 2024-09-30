package com.example.lab8todoist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab8todoist.data.Task
import com.example.lab8todoist.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    fun addTask(description: String, priority: String, category: String) {
        val newTask = Task(description = description, priority = priority, category = category)
        viewModelScope.launch {
            dao.insertTask(newTask)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task.id)
            loadTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
            loadTasks()
        }
    }

    fun filterTasks(isCompleted: Boolean) {
        viewModelScope.launch {
            _tasks.value = dao.getTasksByCompletion(isCompleted)
        }
    }
}
