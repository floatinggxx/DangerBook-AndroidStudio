package com.example.DangerBook.data.remoto.service

import com.example.DangerBook.data.remoto.dto.UsuarioDto
import retrofit2.http.GET

interface UsuarioApiService {
    @GET("api/v1/usuarios")
    suspend fun getUsuarios(): List<UsuarioDto>
}


