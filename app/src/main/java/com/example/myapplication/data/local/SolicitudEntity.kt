package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de datos de una solicitud de cliente.
 *
 * Esta clase es la tabla "solicitudes" de la base de datos local (Room).
 * Pertenece a la capa Model del patron MVVM: solo describe los datos,
 * no sabe nada de la interfaz ni de como se muestran.
 */
@Entity(tableName = "solicitudes")
data class SolicitudEntity(

    /** Clave primaria interna de la base de datos. Room la genera sola. */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Identificador unico visible para el usuario, por ejemplo "SOL-A1B2C3D4". */
    val codigo: String,

    val titulo: String,

    val descripcion: String,

    val categoria: String,

    /** Servicio relacionado. Puede ir vacio cuando la categoria no lo exige. */
    val servicio: String = "",

    /** Ruta de la foto de evidencia ficticia tomada con la camara. Null si no hay. */
    val evidenciaUri: String? = null,

    /** Estado del ciclo de vida de la solicitud. Una solicitud nueva nace "Recibida". */
    val estado: String = "Recibida",

    /** Fecha y hora de creacion, en milisegundos. */
    val fechaCreacion: Long = System.currentTimeMillis()
)
