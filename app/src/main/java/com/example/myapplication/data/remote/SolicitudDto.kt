package com.example.myapplication.data.remote

import com.example.myapplication.data.local.SolicitudEntity
import com.google.gson.annotations.SerializedName

/**
 * Representacion de una solicitud tal como viaja por la API REST.
 *
 * Es distinta de SolicitudEntity a proposito: la entidad es la tabla local
 * (Room) y el DTO es el contrato con el servidor. Asi un cambio en la base
 * local no rompe la API, ni al reves.
 *
 * MongoDB identifica cada documento con "_id" (texto). Ese valor es el
 * remoteId que la app guarda para saber que solicitud local corresponde a
 * cual documento del servidor.
 */
data class SolicitudDto(
    @SerializedName("_id") val id: String? = null,
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val servicio: String = "",
    val evidenciaUri: String? = null,
    val estado: String = "Recibida",
    val fechaCreacion: Long
)

/** Entidad local -> DTO para enviar al servidor. */
fun SolicitudEntity.toDto() = SolicitudDto(
    id = remoteId,
    codigo = codigo,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    servicio = servicio,
    evidenciaUri = evidenciaUri,
    estado = estado,
    fechaCreacion = fechaCreacion
)

/** DTO recibido del servidor -> entidad local. [idLocal] conserva la fila existente, si la hay. */
fun SolicitudDto.toEntity(idLocal: Long = 0, evidenciaLocal: String? = null) = SolicitudEntity(
    id = idLocal,
    codigo = codigo,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    servicio = servicio,
    // La foto vive en el telefono: si el servidor no la tiene, se conserva la local.
    evidenciaUri = evidenciaUri ?: evidenciaLocal,
    estado = estado,
    fechaCreacion = fechaCreacion,
    remoteId = id
)
