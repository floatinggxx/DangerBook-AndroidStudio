package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.horarios.DiaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DiaApiService {
    @GET("api/v1/dias")
    suspend fun findAll(): List<DiaDto>

    @GET("api/v1/dias/{id}")
    suspend fun findById(@Path("id") id: Int): DiaDto

    @POST("api/v1/dias")
    suspend fun save(@Body dia: DiaDto): DiaDto
}
