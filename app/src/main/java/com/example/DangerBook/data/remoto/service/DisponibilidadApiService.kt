package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.horarios.DisponibilidadDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DisponibilidadApiService {
    @GET("api/v1/disponibilidades")
    suspend fun findAll(): List<DisponibilidadDto>

    @GET("api/v1/disponibilidades/{id}")
    suspend fun findById(@Path("id") id: Int): DisponibilidadDto

    @POST("api/v1/disponibilidades")
    suspend fun save(@Body disponibilidad: DisponibilidadDto): DisponibilidadDto
}
