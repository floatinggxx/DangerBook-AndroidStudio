package com.example.DangerBook.data.remoto.dto

//Reemplazo de UsuarioEntity
data class UsuarioDto(
    val id_usuario: Int? = null,
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val telefono: String,
    val contrasena: String,
    val fechaRegistro: String,
    val id_rol: Int,
    val id_estado: Int
)