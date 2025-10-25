package com.example.uinavegacion.data.local.barber

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa a los barberos
@Entity(tableName = "barbers")
data class BarberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val specialty: String, // Especialidad
    val photoUrl: String? = null,
    val rating: Double = 5.0, // Calificación del barbero (0.0 a 5.0)
    val isAvailable: Boolean = true // Si está disponible para agendar
)