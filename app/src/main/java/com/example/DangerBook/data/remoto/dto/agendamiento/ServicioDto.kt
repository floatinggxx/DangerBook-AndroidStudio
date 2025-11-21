package com.example.DangerBook.data.remoto.dto.agendamiento

import com.google.gson.annotations.SerializedName

data class ServicioDto(
    @SerializedName("id_servicio")
    val idServicio: Int? = null,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("foto")
    val foto: String?,
    @SerializedName("precio")
    val precio: String
)
