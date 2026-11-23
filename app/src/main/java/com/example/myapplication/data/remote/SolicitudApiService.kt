package com.example.myapplication.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Contrato HTTP con el backend (que guarda en MongoDB).
 *
 * Igual que ApiService de la guia 14, pero con los cuatro metodos del CRUD.
 * Cada funcion es suspend: se llama desde corrutinas, sin bloquear la UI.
 */
interface SolicitudApiService {

    @GET("solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): SolicitudDto

    @PUT("solicitudes/{id}")
    suspend fun actualizarSolicitud(
        @Path("id") id: String,
        @Body solicitud: SolicitudDto
    ): SolicitudDto

    @DELETE("solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: String)
}
