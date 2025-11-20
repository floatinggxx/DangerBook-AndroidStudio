package com.example.DangerBook.data.remoto.dto.horarios

import com.google.gson.annotations.SerializedName

data class DiaDto(
    @SerializedName("id_dia")
    val id_dia: Int? = null,
    @SerializedName("dia")
    val dia: String? = null
)