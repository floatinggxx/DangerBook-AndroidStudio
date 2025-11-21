package com.example.DangerBook.data.remoto.dto.agendamiento

import com.google.gson.annotations.SerializedName

data class AgendaDto(
    @SerializedName("id_agenda")
    val idAgenda: Int? = null,
    @SerializedName("fecha_solicitud")
    val fechaSolicitud: String,
    @SerializedName("total")
    val total: Double,
    @SerializedName("id_usuario")
    val idUsuario: Long,
    @SerializedName("id_horario")
    val idHorario: Int,
    @SerializedName("detalle")
    val detalle: DetalleDto,
    @SerializedName("servicio")
    val servicio: ServicioDto
)
