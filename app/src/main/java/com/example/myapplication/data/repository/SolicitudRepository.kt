package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.local.SolicitudDao
import com.example.myapplication.data.local.SolicitudEntity
import com.example.myapplication.data.remote.SolicitudApiService
import com.example.myapplication.data.remote.toDto
import com.example.myapplication.data.remote.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: unica fuente de datos para el resto de la aplicacion.
 *
 * Estrategia "local primero": Room es la fuente de verdad que alimenta la
 * UI (siempre funciona, incluso sin internet) y la API REST (MongoDB) se
 * mantiene sincronizada en segundo plano. Si el servidor no responde, la
 * app no falla: la solicitud queda con remoteId = null y se envia en la
 * proxima sincronizacion.
 *
 *   View -> ViewModel -> Repository -> DAO (Room)
 *                                   -> ApiService (Retrofit -> Mongo)
 */
class SolicitudRepository(
    private val dao: SolicitudDao,
    private val api: SolicitudApiService
) {

    val solicitudes: Flow<List<SolicitudEntity>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Long): SolicitudEntity? = dao.obtenerPorId(id)

    suspend fun guardar(solicitud: SolicitudEntity) {
        // Si se edita, se conserva el remoteId que la fila ya tenia.
        val existente = if (solicitud.id != 0L) dao.obtenerPorId(solicitud.id) else null
        val local = solicitud.copy(remoteId = solicitud.remoteId ?: existente?.remoteId)

        val idLocal = if (local.id == 0L) dao.insertar(local) else {
            dao.actualizar(local)
            local.id
        }

        enviarAlServidor(local.copy(id = idLocal))
    }

    suspend fun eliminar(solicitud: SolicitudEntity) {
        dao.eliminar(solicitud)
        solicitud.remoteId?.let { remoteId ->
            try {
                api.eliminarSolicitud(remoteId)
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo eliminar en el servidor: ${e.message}")
            }
        }
    }

    /**
     * Sincroniza con el servidor: primero sube lo pendiente y luego baja lo
     * que haya en Mongo. Nunca lanza excepcion; devuelve false si no hubo red.
     */
    suspend fun sincronizar(): Boolean {
        return try {
            dao.obtenerPendientes().forEach { enviarAlServidor(it) }

            api.obtenerSolicitudes().forEach { dto ->
                val remoteId = dto.id ?: return@forEach
                val local = dao.obtenerPorRemoteId(remoteId)
                if (local == null) {
                    dao.insertar(dto.toEntity())
                } else {
                    dao.actualizar(dto.toEntity(local.id, local.evidenciaUri))
                }
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "Sin sincronizacion: ${e.message}")
            false
        }
    }

    /** Crea (POST) o actualiza (PUT) en el servidor y guarda el remoteId recibido. */
    private suspend fun enviarAlServidor(local: SolicitudEntity) {
        try {
            val respuesta = if (local.remoteId == null) {
                api.crearSolicitud(local.toDto())
            } else {
                api.actualizarSolicitud(local.remoteId, local.toDto())
            }
            if (local.remoteId == null && respuesta.id != null) {
                dao.actualizar(local.copy(remoteId = respuesta.id))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Solicitud ${local.codigo} queda pendiente: ${e.message}")
        }
    }

    private companion object {
        const val TAG = "SolicitudRepository"
    }
}
