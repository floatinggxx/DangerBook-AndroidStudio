package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.horarios.DisponibilidadDto
import com.example.DangerBook.data.remoto.service.DisponibilidadApiService
import com.example.DangerBook.service.UsuarioRemoteModule

class DisponibilidadRepository {

    private val disponibilidadApi: DisponibilidadApiService =
        UsuarioRemoteModule.create(DisponibilidadApiService::class.java)

    suspend fun findAll(): Result<List<DisponibilidadDto>> {
        return try {
            val disponibilidades = disponibilidadApi.findAll()
            Result.success(disponibilidades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<DisponibilidadDto> {
        return try {
            val disponibilidad = disponibilidadApi.findById(id)
            Result.success(disponibilidad)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(disponibilidad: DisponibilidadDto): Result<DisponibilidadDto> {
        return try {
            val savedDisponibilidad = disponibilidadApi.save(disponibilidad)
            Result.success(savedDisponibilidad)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
