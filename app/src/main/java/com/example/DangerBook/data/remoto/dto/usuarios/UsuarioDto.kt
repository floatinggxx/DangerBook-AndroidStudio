package com.example.DangerBook.data.remoto.dto.usuarios

import com.google.gson.annotations.SerializedName

//Reemplazo de UsuarioEntity
data class UsuarioDto(
    @SerializedName("id_usuario")
    val id_usuario: Int? = null,
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val telefono: String,
    val contrasena: String,
    @SerializedName("fechaRegistro")
    val fechaRegistro: String,
    @SerializedName("fotoPerfil")
    val fotoPerfil: ByteArray? = null,
    @SerializedName("id_rol")
    val id_rol: Int,
    @SerializedName("id_estado")
    val id_estado: Int
)