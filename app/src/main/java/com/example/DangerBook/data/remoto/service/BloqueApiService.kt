package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.horarios.BloqueDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BloqueApiService {
    @GET("api/v1/bloques")
    suspend fun findAll(): List<BloqueDto>

    @GET("api/v1/bloques/{id}")
    suspend fun findById(@Path("id") id: Int): BloqueDto

    @POST("api/v1/bloques")
    suspend fun save(@Body bloque: BloqueDto): BloqueDto
}
