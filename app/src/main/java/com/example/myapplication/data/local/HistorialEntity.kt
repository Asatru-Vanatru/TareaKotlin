package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial")
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val solicitudId: Long,
    val estadoAnterior: String,
    val estadoNuevo: String,
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis()
)