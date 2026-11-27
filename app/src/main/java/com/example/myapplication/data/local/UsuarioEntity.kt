package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Usuario ficticio de la demostracion. La clave se guarda en texto plano
 * porque no es una credencial real; una app productiva no deberia hacerlo.
 */
@Entity(tableName = "usuarios", indices = [Index(value = ["nombre"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val clave: String,
    val rol: Rol = Rol.CLIENTE,
    /** true = administrador base: nadie puede cambiar su rol. */
    val protegido: Boolean = false
)
