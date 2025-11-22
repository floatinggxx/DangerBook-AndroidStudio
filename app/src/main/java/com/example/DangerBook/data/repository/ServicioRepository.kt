package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.agendamiento.ServicioDto
import com.example.DangerBook.data.remoto.service.ServicioApiService
import com.example.DangerBook.data.remoto.service.UsuarioRemoteModule

class ServicioRepository {

    private val servicioApi: ServicioApiService =
        UsuarioRemoteModule.create(ServicioApiService::class.java)

    suspend fun findAll(): Result<List<ServicioDto>> {
        return try {
            val servicios = servicioApi.findAll()
            Result.success(servicios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
            Result.success(savedServicio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
