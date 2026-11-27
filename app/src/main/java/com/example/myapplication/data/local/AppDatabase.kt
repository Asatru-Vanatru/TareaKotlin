package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    entities = [SolicitudEntity::class, HistorialEntity::class, UsuarioEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao
    abstract fun historialDao(): HistorialDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("""CREATE TABLE IF NOT EXISTS historial (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                solicitudId INTEGER NOT NULL,
                                estadoAnterior TEXT NOT NULL,
                                estadoNuevo TEXT NOT NULL,
                                comentario TEXT NOT NULL,
                                fecha INTEGER NOT NULL
                            )
                        """.trimIndent())
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS usuarios (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                nombre TEXT NOT NULL,
                                clave TEXT NOT NULL,
                                rol TEXT NOT NULL,
                                protegido INTEGER NOT NULL
                            )
                        """.trimIndent())
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_nombre ON usuarios (nombre)"
                )
                sembrarUsuarios(db)
            }
        }

        /** En una instalacion nueva no corren migraciones: se siembra aqui. */
        private val SEMILLA = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                sembrarUsuarios(db)
            }
        }

        /** Arma la base con sus migraciones. Las pruebas usan otro nombre. */
        fun construir(context: Context, nombre: String): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                nombre
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .addCallback(SEMILLA)
                .build()

        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            // Si ya existe se devuelve; si no, se crea dentro de un bloque
            // sincronizado para que dos hilos no la creen al mismo tiempo.
            return INSTANCIA ?: synchronized(this) {
                val instancia = construir(context, "solicitudes.db")
                INSTANCIA = instancia
                instancia
            }
        }
    }
}
