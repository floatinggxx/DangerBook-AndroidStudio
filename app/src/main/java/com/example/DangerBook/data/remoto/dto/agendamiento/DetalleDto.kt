package com.example.DangerBook.data.remoto.dto.agendamiento

import com.google.gson.annotations.SerializedName

data class DetalleDto(
    @SerializedName("id_detalle")
    val idDetalle: Int? = null,
    @SerializedName("subtotal")
    val subtotal: String
)
