package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observarPorId(id: Long): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuarios ORDER BY protegido DESC, nombre")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("UPDATE usuarios SET rol = :rol WHERE id = :id")
    suspend fun actualizarRol(id: Long, rol: Rol)
}
