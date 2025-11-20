package com.example.DangerBook.data.remoto.dto.usuarios

import com.google.gson.annotations.SerializedName

data class EstadoDto(
    @SerializedName("id_estado")
    val id_estado: Int? = null,
    @SerializedName("nombre_estado")
    val nombre_estado: String? = null
)