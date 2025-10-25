package com.example.uinavegacion.data.local.service

import androidx.room.Entity
import androidx.room.PrimaryKey

// Representa los servicios disponibles en la barbería
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String, // Nombre del servicio
    val description: String, // Descripción detallada del servicio
    val price: Double, // Precio
    val durationMinutes: Int, // Duración aproximada en minutos
    val imageUrl: String? = null, // URL de imagen (opcional, puede ser null)
    val isActive: Boolean = true // Si el servicio está activo/disponible
)