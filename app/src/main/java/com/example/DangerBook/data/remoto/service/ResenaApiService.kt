package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.resenas.ResenaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ResenaApiService {
    @GET("api/v1/resenas")
    suspend fun findAll(): List<ResenaDto>

    @GET("api/v1/resenas/{id}")
    suspend fun findById(@Path("id") id: Int): ResenaDto

    @POST("api/v1/resenas")
    suspend fun save(@Body resena: ResenaDto): ResenaDto

    @DELETE("api/v1/resenas/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Void>
}
