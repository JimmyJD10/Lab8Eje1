package com.example.lab8todoist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.lab8todoist.data.TaskDatabase
import com.example.lab8todoist.viewmodel.TaskViewModel
import com.example.lab8todoist.ui.TaskScreen
import com.example.lab8todoist.ui.theme.Lab8TodoistTheme
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canal de notificaciones
        createNotificationChannel()

        setContent {
            Lab8TodoistTheme {
                var selectedTab by remember { mutableStateOf("Tasks") }

                Column(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        "Tasks" -> {
                            val db = TaskDatabase.getDatabase(this@MainActivity)
                            val taskDao = db.taskDao()
                            val viewModel = TaskViewModel(taskDao)
                            TaskScreen(viewModel)
                        }
                        "Home" -> {
                            Text("Home Screen", modifier = Modifier.padding(16.dp))
                        }
                        "Settings" -> {
                            Text("Settings Screen", modifier = Modifier.padding(16.dp))
                        }
                    }

                    // Botones para cambiar entre pantallas
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                        Button(onClick = { selectedTab = "Tasks" }) {
                            Text("Tasks")
                        }
                        Button(onClick = { selectedTab = "Home" }) {
                            Text("Home")
                        }
                        Button(onClick = { selectedTab = "Settings" }) {
                            Text("Settings")
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Canal para notificaciones de tareas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TASK_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Función para enviar notificaciones
    fun sendNotification(context: Context, taskDescription: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "TASK_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Tarea pendiente")
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, builder.build())
    }
}
