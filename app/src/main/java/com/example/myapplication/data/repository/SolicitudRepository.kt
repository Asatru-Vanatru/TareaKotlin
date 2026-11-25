package com.example.myapplication.data.repository

import com.example.myapplication.data.local.SolicitudDao
import com.example.myapplication.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow
import com.example.myapplication.data.local.HistorialDao
import com.example.myapplication.data.local.HistorialEntity
import androidx.room.withTransaction
import com.example.myapplication.data.local.AppDatabase

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
class SolicitudRepository(
    private val dao: SolicitudDao,
    private val historialDao: HistorialDao,
    private val baseDeDatos: AppDatabase
) {


    val solicitudes: Flow<List<SolicitudEntity>> = dao.obtenerTodas()
    val historial: Flow<List<HistorialEntity>> = historialDao.obtenerHistorial()

    suspend fun obtenerPorId(id: Long): SolicitudEntity? = dao.obtenerPorId(id)

    suspend fun guardar(solicitud: SolicitudEntity) {
        baseDeDatos.withTransaction {
            // id == 0 significa que la solicitud todavia no existe en la base,
            // por lo tanto se inserta. Si ya tiene id, se actualiza.
            if (solicitud.id == 0L) {
                val nuevoId = dao.insertar(solicitud)
                historialDao.insertar(
                    HistorialEntity(
                        solicitudId = nuevoId,
                        estadoAnterior = "Sin estado",
                        estadoNuevo = solicitud.estado,
                        comentario = "Solicitud creada"
                    )
                )
            } else {
                val solicitudAnterior = dao.obtenerPorId(solicitud.id)
                dao.actualizar(solicitud)
                if (solicitudAnterior != null) {
                    historialDao.insertar(
                        HistorialEntity(
                            solicitudId = solicitud.id,
                            estadoAnterior = solicitudAnterior.estado,
                            estadoNuevo = solicitud.estado,
                            comentario = "Solicitud modificada"
                        )
                    )
                }
            }
        }
    }//aca

    suspend fun eliminar(solicitud: SolicitudEntity) = dao.eliminar(solicitud)
}
