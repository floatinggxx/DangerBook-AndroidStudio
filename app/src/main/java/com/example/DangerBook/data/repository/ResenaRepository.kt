package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.resenas.ResenaDto
import com.example.DangerBook.data.remoto.service.ResenaApiService
import com.example.DangerBook.data.remoto.UsuarioRemoteModule

class ResenaRepository {

    private val resenaApi: ResenaApiService =
        UsuarioRemoteModule.create(ResenaApiService::class.java)

    suspend fun findAll(): Result<List<ResenaDto>> {
        return try {
            val resenas = resenaApi.findAll()
            Result.success(resenas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<ResenaDto> {
        return try {
            val resena = resenaApi.findById(id)
            Result.success(resena)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(resena: ResenaDto): Result<ResenaDto> {
        return try {
            val savedResena = resenaApi.save(resena)
            Result.success(savedResena)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: Int): Result<Unit> {
        return try {
            val response = resenaApi.delete(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar la reseña: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
