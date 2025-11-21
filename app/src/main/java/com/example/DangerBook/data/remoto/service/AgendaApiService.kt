package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.agendamiento.AgendaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AgendaApiService {
    @GET("api/v1/agendas")
    suspend fun findAll(): List<AgendaDto>

    @GET("api/v1/agendas/{id}")
    suspend fun findById(@Path("id") id: Int): AgendaDto

    @POST("api/v1/agendas")
    suspend fun save(@Body agenda: AgendaDto): AgendaDto

    @DELETE("api/v1/agendas/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Void>
}
