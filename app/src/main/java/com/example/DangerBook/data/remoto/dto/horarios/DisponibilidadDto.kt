package com.example.DangerBook.data.remoto.dto.horarios

import com.google.gson.annotations.SerializedName

data class DisponibilidadDto(
    @SerializedName("id_disponibilidad")
    val id_disponibilidad: Int? = null,
    @SerializedName("id_horario")
    val id_horario: Int? = null,
    @SerializedName("id_usuario")
    val id_usuario: Int? = null
)