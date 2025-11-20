package com.example.DangerBook.data.remoto.dto.usuarios

import com.google.gson.annotations.SerializedName

data class RolDto(
    @SerializedName("id_rol")
    val id_rol: Int? = null,
    @SerializedName("nombre")
    val nombre: String
)