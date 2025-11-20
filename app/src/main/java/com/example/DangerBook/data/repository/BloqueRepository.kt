package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.horarios.BloqueDto
import com.example.DangerBook.data.remoto.service.BloqueApiService
import com.example.DangerBook.service.UsuarioRemoteModule

class BloqueRepository {

    private val bloqueApi: BloqueApiService =
        UsuarioRemoteModule.create(BloqueApiService::class.java)

    suspend fun findAll(): Result<List<BloqueDto>> {
        return try {
            val bloques = bloqueApi.findAll()
            Result.success(bloques)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<BloqueDto> {
        return try {
            val bloque = bloqueApi.findById(id)
            Result.success(bloque)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(bloque: BloqueDto): Result<BloqueDto> {
        return try {
            val savedBloque = bloqueApi.save(bloque)
            Result.success(savedBloque)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
