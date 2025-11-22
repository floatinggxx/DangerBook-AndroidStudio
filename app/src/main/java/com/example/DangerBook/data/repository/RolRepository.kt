package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.usuarios.RolDto
import com.example.DangerBook.data.remoto.service.RolApiService
import com.example.DangerBook.data.remoto.UsuarioRemoteModule

class RolRepository {

    private val rolApi: RolApiService =
        UsuarioRemoteModule.create(RolApiService::class.java)

    suspend fun findAll(): Result<List<RolDto>> {
        return try {
            val roles = rolApi.findAll()
            Result.success(roles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<RolDto> {
        return try {
            val rol = rolApi.findById(id)
            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(rol: RolDto): Result<RolDto> {
        return try {
            val savedRol = rolApi.save(rol)
            Result.success(savedRol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
