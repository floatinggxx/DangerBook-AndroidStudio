package com.example.DangerBook.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.DangerBook.data.remoto.dto.resenas.ResenaDto
import com.example.DangerBook.ui.viewmodel.ResenaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResenasScreen(
    vm: ResenaViewModel
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            Toast.makeText(context, "¡Gracias por tu reseña!", Toast.LENGTH_SHORT).show()
            vm.resetSubmissionStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas de la Barbería") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NewResenaForm(vm = vm, isSubmitting = uiState.isSubmitting)
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Text(
                    text = "Lo que dicen nuestros clientes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.isLoading) {
                item { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.errorMsg != null) {
                item { Text(text = uiState.errorMsg!!, color = MaterialTheme.colorScheme.error) }
            } else if (uiState.resenas.isEmpty()) {
                item { Text("Todavía no hay reseñas. ¡Sé el primero!") }
            } else {
                items(uiState.resenas) { resena ->
                    ResenaCard(resena = resena)
                }
            }
        }
    }
}

@Composable
private fun NewResenaForm(
    vm: ResenaViewModel,
    isSubmitting: Boolean
) {
    var comentario by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Deja tu opinión",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it },
            label = { Text("Escribe tu comentario...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Calificación:", style = MaterialTheme.typography.bodyLarge)
        RatingBar(rating = calificacion, onRatingChanged = { calificacion = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.submitResena(comentario, calificacion) },
            enabled = !isSubmitting && comentario.isNotBlank() && calificacion > 0,
            modifier = Modifier.align(Alignment.End)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Reseña")
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row {
        (1..5).forEach { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "$index estrellas",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(index) },
                tint = if (index <= rating) Color(0xFFFFD700) else Color.Gray
            )
        }
    }
}

@Composable
private fun ResenaCard(resena: ResenaDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    repeat(resena.calificacion ?: 0) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                    }
                    repeat(5 - (resena.calificacion ?: 0)) {
                         Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
                // CORRECCIÓN: Manejar el caso en que la fecha es nula
                val formattedDate = if (!resena.f_publicacion.isNullOrBlank()) {
                    try {
                        LocalDate.parse(resena.f_publicacion).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } catch (e: Exception) {
                        resena.f_publicacion // Fallback si el formato es inesperado
                    }
                } else {
                    "Fecha no disponible"
                }
                Text(text = formattedDate, style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = resena.comentario ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}