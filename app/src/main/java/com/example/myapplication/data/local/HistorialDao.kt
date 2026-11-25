package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface HistorialDao   {
    @Insert
    suspend fun insertar(historial: HistorialEntity)
    @Query("SELECT * FROM historial ORDER BY fecha DESC")
    fun obtenerHistorial(): Flow<List<HistorialEntity>>

    @Query("SELECT * FROM historial WHERE solicitudId = :solicitudId ORDER BY fecha DESC")
    fun obtenerPorSolicitud(solicitudId: Long ): Flow<List<HistorialEntity>>
}
