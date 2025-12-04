package com.example.DangerBook.data.remoto.dto.resenas

import com.google.gson.annotations.SerializedName

data class ResenaDto(
    @SerializedName("idResena")
    val id_resena: Int? = null,
    @SerializedName("fPublicacion")
    val f_publicacion: String?,
    @SerializedName("comentario")
    val comentario: String?,
    @SerializedName("calificacion")
    val calificacion: Int?,
    @SerializedName("fBaneo")
    val f_baneo: String?,
    @SerializedName("motivoBaneo")
    val motivo_baneo: String?
)
