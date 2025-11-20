package com.example.DangerBook.data.remoto.dto.horarios

import com.google.gson.annotations.SerializedName

data class HorarioDto(
    @SerializedName("id_horario")
    val id_horario: Int? = null,
    @SerializedName("id_dia")
    val id_dia: Int? = null,
    @SerializedName("id_bloque")
    val id_bloque: Int? = null
)