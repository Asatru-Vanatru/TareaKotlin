package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local de la aplicacion.
 *
 * Es el punto de entrada a Room. Se construye una sola vez por aplicacion
 * (patron singleton) porque abrir varias conexiones al mismo archivo es caro
 * y puede provocar inconsistencias.
 *
 * Al estar en disco, los datos siguen disponibles despues de cerrar y volver
 * a abrir la app: eso es lo que exige la pauta cuando prohibe guardar en una
 * lista en memoria.
 */
@Database(
    entities = [SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao

    companion object {

        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            // Si ya existe se devuelve; si no, se crea dentro de un bloque
            // sincronizado para que dos hilos no la creen al mismo tiempo.
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solicitudes.db"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}
