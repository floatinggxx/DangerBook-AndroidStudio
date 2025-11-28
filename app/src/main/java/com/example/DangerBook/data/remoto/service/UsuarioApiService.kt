package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.usuarios.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioApiService {
    @GET("api/v1/usuarios")
    suspend fun findAll(): List<UsuarioDto>

    @GET("api/v1/usuarios/{id}")
    suspend fun findById(@Path("id") id: Int): UsuarioDto

    @POST("api/v1/usuarios")
    suspend fun save(@Body usuario: UsuarioDto): UsuarioDto

    @POST("api/v1/usuarios/login")
    suspend fun login(@Body body: Map<String, String>): UsuarioDto

    @POST("api/v1/usuarios/reset-password")
    suspend fun resetPassword(@Body body: Map<String, String>): Response<Unit>
}
