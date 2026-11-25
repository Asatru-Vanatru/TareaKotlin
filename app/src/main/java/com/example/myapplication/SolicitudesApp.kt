package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.SolicitudRepository

/**
 * Clase Application de la app.
 *
 * Aqui se crean la base de datos y el repositorio una sola vez, cuando el
 * proceso arranca. El ViewModel los toma desde aca mediante su Factory, de
 * forma que ninguna pantalla tenga que construir la base de datos por su
 * cuenta.
 *
 * "by lazy" significa que no se crean hasta la primera vez que se usan.
 */
class SolicitudesApp : Application() {

    private val baseDeDatos by lazy { AppDatabase.obtenerInstancia(this) }

    val repositorio by lazy { SolicitudRepository(baseDeDatos.solicitudDao()) }
}
