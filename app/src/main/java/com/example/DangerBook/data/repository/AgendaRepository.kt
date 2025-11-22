package com.example.DangerBook.data.repository

import com.example.DangerBook.data.remoto.dto.agendamiento.AgendaDto
import com.example.DangerBook.data.remoto.service.AgendaApiService
import com.example.DangerBook.data.remoto.service.UsuarioRemoteModule

class AgendaRepository {

    private val agendaApi: AgendaApiService =
        UsuarioRemoteModule.create(AgendaApiService::class.java)

    suspend fun findAll(): Result<List<AgendaDto>> {
        return try {
            val agendas = agendaApi.findAll()
            Result.success(agendas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: Int): Result<AgendaDto> {
        return try {
            val agenda = agendaApi.findById(id)
            Result.success(agenda)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(agenda: AgendaDto): Result<AgendaDto> {
        return try {
            val savedAgenda = agendaApi.save(agenda)
            Result.success(savedAgenda)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: Int): Result<Unit> {
        return try {
            val response = agendaApi.delete(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar la agenda: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
