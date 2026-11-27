package com.example.myapplication.data.local

import androidx.sqlite.db.SupportSQLiteDatabase

/** Usuarios ficticios que existen desde la instalacion. */
val USUARIOS_BASE = listOf(
    administradorBase("Amanda"),
    administradorBase("Maira"),
    administradorBase("Benjamin"),
    UsuarioEntity(nombre = "Tecnico", clave = "Tecnico", rol = Rol.TECNICO),
    UsuarioEntity(nombre = "Cliente", clave = "Cliente"),
    UsuarioEntity(nombre = "Tester", clave = "Tester")
)

private fun administradorBase(nombre: String) =
    UsuarioEntity(nombre = nombre, clave = nombre, rol = Rol.ADMIN, protegido = true)

/**
 * Inserta los usuarios base. OR IGNORE evita duplicados y no pisa los roles
 * de usuarios que ya existan.
 */
fun sembrarUsuarios(db: SupportSQLiteDatabase) {
    USUARIOS_BASE.forEach { usuario ->
        db.execSQL(
            "INSERT OR IGNORE INTO usuarios (nombre, clave, rol, protegido) VALUES (?, ?, ?, ?)",
            arrayOf<Any>(
                usuario.nombre,
                usuario.clave,
                usuario.rol.name,
                if (usuario.protegido) 1 else 0
            )
        )
    }
}
