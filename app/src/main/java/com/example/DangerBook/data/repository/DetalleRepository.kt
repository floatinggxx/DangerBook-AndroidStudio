package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.agendamiento.DetalleDto
import com.example.DangerBook.data.remoto.service.DetalleApiService
import com.example.DangerBook.service.UsuarioRemoteModule

class DetalleRepository {

    private val detalleApi: DetalleApiService =
        UsuarioRemoteModule.create(DetalleApiService::class.java)

    suspend fun findAll(): Result<List<DetalleDto>> {
        return try {
            val detalles = detalleApi.findAll()
            Result.success(detalles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<DetalleDto> {
        return try {
            val detalle = detalleApi.findById(id)
            Result.success(detalle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(detalle: DetalleDto): Result<DetalleDto> {
        return try {
            val savedDetalle = detalleApi.save(detalle)
            Result.success(savedDetalle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
