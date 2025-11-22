package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.horarios.DiaDto
import com.example.DangerBook.data.remoto.service.DiaApiService
import com.example.DangerBook.data.remoto.service.UsuarioRemoteModule

class DiaRepository {

    private val diaApi: DiaApiService =
        UsuarioRemoteModule.create(DiaApiService::class.java)

    suspend fun findAll(): Result<List<DiaDto>> {
        return try {
            val dias = diaApi.findAll()
            Result.success(dias)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<DiaDto> {
        return try {
            val dia = diaApi.findById(id)
            Result.success(dia)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(dia: DiaDto): Result<DiaDto> {
        return try {
            val savedDia = diaApi.save(dia)
            Result.success(savedDia)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
