package com.example.DangerBook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import com.example.DangerBook.ui.viewmodel.AppointmentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyAppointmentsScreen(
    vm: AppointmentViewModel,
    onBookNewAppointment: () -> Unit
) {
    val state by vm.myAppointmentsState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onBookNewAppointment) {
                Icon(Icons.Filled.Add, contentDescription = "Agendar nueva cita")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMsg != null -> {
                    Text(
                        text = state.errorMsg ?: "Error desconocido",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.allAppointments.isEmpty() -> {
                    EmptyState(onBookNewAppointment)
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.allAppointments) { appointment ->
                            AppointmentCard(appointment = appointment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentEntity) {
    val dateFormatter = remember { SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("es", "ES")) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val appointmentDate = Date(appointment.dateTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Cita Agendada", // Placeholder, idealmente vendría del service
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = appointment.status)
            }

            Spacer(Modifier.height(12.dp))

            // Detalles de la cita
            DetailRow(icon = Icons.Filled.CalendarMonth, text = dateFormatter.format(appointmentDate).replaceFirstChar { it.uppercase() })
            DetailRow(icon = Icons.Filled.Info, text = "Hora: ${timeFormatter.format(appointmentDate)}")
            DetailRow(icon = Icons.Filled.ContentCut, text = "Duración: ${appointment.durationMinutes} min")
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.size(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun StatusBadge(status: String) {
    val (text, color) = when (status.lowercase()) {
        "pending" -> "Pendiente" to Color(0xFFFFA000) // Amber
        "confirmed" -> "Confirmada" to Color(0xFF388E3C) // Green
        "completed" -> "Completada" to Color(0xFF1976D2) // Blue
        "cancelled" -> "Cancelada" to Color.Red
        else -> status to Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = color, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun EmptyState(onBookNew: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No tienes citas agendadas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Parece que todavía no tienes ninguna cita. ¡Agenda una para empezar!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBookNew) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Agendar mi primera cita")
        }
    }
}