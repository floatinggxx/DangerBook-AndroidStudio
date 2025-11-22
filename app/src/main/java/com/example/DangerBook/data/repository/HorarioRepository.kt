package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.horarios.HorarioDto
import com.example.DangerBook.data.remoto.service.HorarioApiService
import com.example.DangerBook.data.remoto.service.UsuarioRemoteModule

class HorarioRepository {

    private val horarioApi: HorarioApiService =
        UsuarioRemoteModule.create(HorarioApiService::class.java)

    suspend fun findAll(): Result<List<HorarioDto>> {
        return try {
            val horarios = horarioApi.findAll()
            Result.success(horarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<HorarioDto> {
        return try {
            val horario = horarioApi.findById(id)
            Result.success(horario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(horario: HorarioDto): Result<HorarioDto> {
        return try {
            val savedHorario = horarioApi.save(horario)
            Result.success(savedHorario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
