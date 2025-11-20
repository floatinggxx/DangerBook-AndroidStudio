package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.horarios.HorarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HorarioApiService {
    @GET("api/v1/horarios")
    suspend fun findAll(): List<HorarioDto>

    @GET("api/v1/horarios/{id}")
    suspend fun findById(@Path("id") id: Int): HorarioDto

    @POST("api/v1/horarios")
    suspend fun save(@Body horario: HorarioDto): HorarioDto
}
