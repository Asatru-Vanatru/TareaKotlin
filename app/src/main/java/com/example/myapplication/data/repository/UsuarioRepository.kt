package com.example.myapplication.data.repository

import com.example.myapplication.data.local.Rol
import com.example.myapplication.data.local.UsuarioDao
import com.example.myapplication.data.local.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * Solo un administrador base puede cambiar roles, nunca los de otro
 * administrador base (ni el suyo) y sin nombrar administradores nuevos.
 */
fun puedeCambiarRol(actor: UsuarioEntity, objetivo: UsuarioEntity, nuevoRol: Rol): Boolean =
    actor.rol == Rol.ADMIN && actor.protegido &&
        !objetivo.protegido &&
        nuevoRol != Rol.ADMIN

class UsuarioRepository(private val dao: UsuarioDao) {

    val usuarios: Flow<List<UsuarioEntity>> = dao.observarTodos()

    fun observarUsuario(id: Long): Flow<UsuarioEntity?> = dao.observarPorId(id)

    /** Devuelve el usuario si las credenciales coinciden; si no, null. */
    suspend fun autenticar(nombre: String, clave: String): UsuarioEntity? =
        dao.buscarPorNombre(nombre.trim())?.takeIf { it.clave == clave }

    /**
     * Los permisos se comprueban con los datos guardados, no con lo que
     * muestre la pantalla. Devuelve false si el cambio no esta permitido.
     */
    suspend fun cambiarRol(actorId: Long, objetivoId: Long, nuevoRol: Rol): Boolean {
        val actor = dao.obtenerPorId(actorId) ?: return false
        val objetivo = dao.obtenerPorId(objetivoId) ?: return false
        if (!puedeCambiarRol(actor, objetivo, nuevoRol)) return false
        dao.actualizarRol(objetivoId, nuevoRol)
        return true
    }
}
