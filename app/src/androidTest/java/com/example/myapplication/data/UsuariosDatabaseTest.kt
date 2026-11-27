package com.example.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.Rol
import com.example.myapplication.data.local.USUARIOS_BASE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Usa una base propia para no tocar los datos de la app. */
class UsuariosDatabaseTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val nombre = "prueba-usuarios.db"

    @Before
    @After
    fun limpiar() {
        context.deleteDatabase(nombre)
    }

    @Test
    fun instalacionNueva_creaUsuariosBase_yConservaRolesAlReabrir() = runBlocking {
        var base = AppDatabase.construir(context, nombre)
        val tester = base.usuarioDao().buscarPorNombre("Tester")!!
        assertEquals(USUARIOS_BASE.size, base.usuarioDao().observarTodos().first().size)

        base.usuarioDao().actualizarRol(tester.id, Rol.TECNICO)
        base.close()

        base = AppDatabase.construir(context, nombre)
        assertEquals(Rol.TECNICO, base.usuarioDao().buscarPorNombre("Tester")!!.rol)
        assertEquals(USUARIOS_BASE.size, base.usuarioDao().observarTodos().first().size)
        base.close()
    }

    @Test
    fun migracionDesdeVersion2_conservaSolicitudes_yCreaUsuarios() = runBlocking {
        crearBaseVersion2()

        // Room valida el esquema al abrir: falla si la migracion no coincide.
        val base = AppDatabase.construir(context, nombre)

        assertEquals(USUARIOS_BASE.size, base.usuarioDao().observarTodos().first().size)
        assertEquals("SOL-PRUEBA", base.solicitudDao().obtenerPorId(1)!!.codigo)
        base.close()
    }

    private fun crearBaseVersion2() {
        val archivo = context.getDatabasePath(nombre)
        archivo.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(archivo, null).use { db ->
            db.execSQL(
                "CREATE TABLE solicitudes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "codigo TEXT NOT NULL, titulo TEXT NOT NULL, descripcion TEXT NOT NULL, " +
                    "categoria TEXT NOT NULL, servicio TEXT NOT NULL, evidenciaUri TEXT, " +
                    "estado TEXT NOT NULL, fechaCreacion INTEGER NOT NULL)"
            )
            db.execSQL(
                "CREATE TABLE historial (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "solicitudId INTEGER NOT NULL, estadoAnterior TEXT NOT NULL, " +
                    "estadoNuevo TEXT NOT NULL, comentario TEXT NOT NULL, fecha INTEGER NOT NULL)"
            )
            db.execSQL(
                "INSERT INTO solicitudes VALUES " +
                    "(1, 'SOL-PRUEBA', 't', 'd', 'Reclamo', 's', NULL, 'Recibida', 0)"
            )
            db.version = 2
        }
    }
}
