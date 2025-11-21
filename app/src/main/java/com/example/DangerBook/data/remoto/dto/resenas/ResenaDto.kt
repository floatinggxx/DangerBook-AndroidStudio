package com.example.DangerBook.data.remoto.dto.resenas

import com.google.gson.annotations.SerializedName

data class ResenaDto(
    @SerializedName("id_resena")
    val id_resena: Int? = null,
    @SerializedName("f_publicacion")
    val f_publicacion: String,
    @SerializedName("comentario")
    val comentario: String?,
    @SerializedName("calificacion")
    val calificacion: Int?,
    @SerializedName("f_baneo")
    val f_baneo: String?,
    @SerializedName("motivo_baneo")
    val motivo_baneo: String?
)
