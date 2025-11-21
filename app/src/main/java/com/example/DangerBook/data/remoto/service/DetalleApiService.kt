package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.agendamiento.DetalleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DetalleApiService {
    @GET("api/v1/detalles")
    suspend fun findAll(): List<DetalleDto>

    @GET("api/v1/detalles/{id}")
    suspend fun findById(@Path("id") id: Int): DetalleDto

    @POST("api/v1/detalles")
    suspend fun save(@Body detalle: DetalleDto): DetalleDto
}
