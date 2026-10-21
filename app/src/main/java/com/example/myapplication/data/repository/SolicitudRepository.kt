package com.example.myapplication.data.repository

import com.example.myapplication.data.local.SolicitudDao
import com.example.myapplication.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: unica fuente de datos para el resto de la aplicacion.
 *
 * Su razon de existir es que el ViewModel NO hable directamente con Room.
 * Si manana las solicitudes vinieran de una API en vez de la base local,
 * solo cambiaria esta clase y ni el ViewModel ni las pantallas se tocarian.
 *
 * Esta es la separacion que la pauta llama "MVVM real":
 * View -> ViewModel -> Repository -> DAO -> Room
 */
class SolicitudRepository(private val dao: SolicitudDao) {

    val solicitudes: Flow<List<SolicitudEntity>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Long): SolicitudEntity? = dao.obtenerPorId(id)

    suspend fun guardar(solicitud: SolicitudEntity) {
        // id == 0 significa que la solicitud todavia no existe en la base,
        // por lo tanto se inserta. Si ya tiene id, se actualiza.
        if (solicitud.id == 0L) {
            dao.insertar(solicitud)
        } else {
            dao.actualizar(solicitud)
        }
    }

    suspend fun eliminar(solicitud: SolicitudEntity) = dao.eliminar(solicitud)
}
