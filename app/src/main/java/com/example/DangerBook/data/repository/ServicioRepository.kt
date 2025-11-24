package com.example.DangerBook.data.repository

import android.util.Log
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.local.service.ServiceDao
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.DangerBook.data.local.user.UserDao
import com.example.DangerBook.data.remoto.AgendamientoRemoteModule
import com.example.DangerBook.data.remoto.dto.agendamiento.ServicioDto
import com.example.DangerBook.data.remoto.service.ServicioApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServicioRepository(
    private val serviceDao: ServiceDao,
    private val userDao: UserDao
) {

    private val servicioApi: ServicioApiService =
        AgendamientoRemoteModule.create(ServicioApiService::class.java)

    // Flujo de datos desde la base de datos local (fuente de verdad para la UI)
    fun getAllServices(): Flow<List<ServiceEntity>> {
        return serviceDao.getAllActive()
    }

    // Obtener todos los barberos disponibles desde la tabla de usuarios
    fun getAllAvailableBarbers(): Flow<List<BarberEntity>> {
        return userDao.getAllBarbers().map { userEntities ->
            userEntities.map { user ->
                BarberEntity(
                    id = user.id,
                    name = user.name,
                    specialty = "Barbero", // Placeholder
                    photoUrl = user.photoUri,
                    rating = 5.0, // Default value
                    isAvailable = true // Assume available
                )
            }
        }
    }

    // Función para refrescar los datos desde la API
    suspend fun refreshServices(): Result<Unit> {
        return try {
            // 1. Obtener servicios de la API
            val remoteServices = servicioApi.findAll()
            Log.d("ServicioRepository", "Servicios recibidos de la API: $remoteServices")


            // 2. Mapear DTOs a Entidades locales
            val serviceEntities = remoteServices.map {
                ServiceEntity(
                    id = it.idServicio?.toLong() ?: 0L,
                    name = it.nombre,
                    description = it.descripcion,
                    price = it.precio.toDoubleOrNull() ?: 0.0, // Conversión segura
                    durationMinutes = 30 // Valor por defecto, ajustar si la API lo provee
                )
            }

            // 3. Limpiar la tabla local y guardar los nuevos datos
            serviceDao.deleteAll()
            serviceDao.insertAll(serviceEntities)

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("ServicioRepository", "Error al refrescar servicios: ${e.message}", e)
            Result.failure(e)
        }
    }

    // --- Métodos remotos adicionales (si son necesarios) ---

    suspend fun findById(id: Int): Result<ServicioDto> {
        return try {
            val servicio = servicioApi.findById(id)
            Result.success(servicio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(servicio: ServicioDto): Result<ServicioDto> {
        return try {
            val savedServicio = servicioApi.save(servicio)
            // Opcional: refrescar la lista después de guardar un nuevo servicio
            refreshServices()
            Result.success(savedServicio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}