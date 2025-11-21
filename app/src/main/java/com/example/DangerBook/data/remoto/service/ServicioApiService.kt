package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.agendamiento.ServicioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServicioApiService {
    @GET("api/v1/servicios")
    suspend fun findAll(): List<ServicioDto>

    @GET("api/v1/servicios/{id}")
    suspend fun findById(@Path("id") id: Int): ServicioDto

    @POST("api/v1/servicios")
    suspend fun save(@Body servicio: ServicioDto): ServicioDto
}
