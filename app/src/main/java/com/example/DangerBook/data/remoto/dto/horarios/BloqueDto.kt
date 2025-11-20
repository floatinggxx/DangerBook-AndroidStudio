package com.example.DangerBook.data.remoto.dto.horarios

import com.google.gson.annotations.SerializedName

data class BloqueDto(
    @SerializedName("id_bloque")
    val id_bloque: Int? = null,
    @SerializedName("fechaInicio")
    val fechaInicio: String,
    @SerializedName("fechaFin")
    val fechaFin: String
)
