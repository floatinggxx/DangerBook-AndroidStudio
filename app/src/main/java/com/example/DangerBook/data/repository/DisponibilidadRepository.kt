package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.horarios.DisponibilidadDto
import com.example.DangerBook.data.remoto.HorarioRemoteModule
import com.example.DangerBook.data.remoto.service.DisponibilidadApiService

class DisponibilidadRepository {

    private val disponibilidadApi: DisponibilidadApiService =
        HorarioRemoteModule.create(DisponibilidadApiService::class.java)

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

    suspend fun getAvailableHours(barberId: Long, date: String): Result<List<String>> {
        return try {
            val hours = disponibilidadApi.getAvailableHours(barberId, date)
            Result.success(hours)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
