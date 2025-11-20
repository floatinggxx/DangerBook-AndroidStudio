package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.usuarios.RolDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RolApiService {
    @GET("api/v1/roles")
    suspend fun findAll(): List<RolDto>

    @GET("api/v1/roles/{id}")
    suspend fun findById(@Path("id") id: Int): RolDto

    @POST("api/v1/roles")
    suspend fun save(@Body rol: RolDto): RolDto
}
