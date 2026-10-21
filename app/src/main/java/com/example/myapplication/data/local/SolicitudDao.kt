package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): define QUE operaciones se pueden hacer sobre la
 * tabla "solicitudes". Room genera la implementacion en tiempo de compilacion.
 *
 * Las consultas de lectura devuelven Flow, de modo que cualquier cambio en la
 * base de datos llega solo a la interfaz. De ahi viene la reactividad que pide
 * la pauta: al guardar una solicitud, la lista se actualiza sin recargar nada.
 */
@Dao
interface SolicitudDao {

    @Query("SELECT * FROM solicitudes ORDER BY fechaCreacion DESC")
    fun obtenerTodas(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Long): SolicitudEntity?

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)

    @Delete
    suspend fun eliminar(solicitud: SolicitudEntity)
}
