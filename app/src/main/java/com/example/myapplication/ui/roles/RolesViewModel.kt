package com.example.myapplication.ui.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.SolicitudesApp
import com.example.myapplication.data.local.Rol
import com.example.myapplication.data.local.UsuarioEntity
import com.example.myapplication.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RolesViewModel(private val repositorio: UsuarioRepository) : ViewModel() {

    val usuarios: StateFlow<List<UsuarioEntity>> = repositorio.usuarios.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cambiarRol(actorId: Long, objetivo: UsuarioEntity, nuevoRol: Rol) {
        viewModelScope.launch {
            val permitido = repositorio.cambiarRol(actorId, objetivo.id, nuevoRol)
            _mensaje.value = if (permitido) {
                "${objetivo.nombre} ahora es ${nuevoRol.etiqueta}"
            } else {
                "No tienes permiso para cambiar ese rol"
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SolicitudesApp
                RolesViewModel(app.repositorioUsuarios)
            }
        }
    }
}
