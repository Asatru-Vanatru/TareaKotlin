package com.example.myapplication.ui.sesion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.SolicitudesApp
import com.example.myapplication.data.local.UsuarioEntity
import com.example.myapplication.data.repository.UsuarioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SesionUiState(
    val usuario: UsuarioEntity? = null,
    val ingresando: Boolean = false,
    val error: String? = null
)

/**
 * Usuario que ingreso a la app. Se observa desde la base, de modo que un
 * cambio de rol se refleja en los menus. SavedStateHandle conserva la sesion
 * si Android cierra el proceso en segundo plano.
 */
class SesionViewModel(
    private val repositorio: UsuarioRepository,
    private val estadoGuardado: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    private var observacion: Job? = null

    init {
        estadoGuardado.get<Long>(CLAVE_USUARIO)?.let(::observar)
    }

    fun iniciarSesion(nombre: String, clave: String) {
        if (_uiState.value.ingresando) return
        _uiState.value = SesionUiState(ingresando = true)

        viewModelScope.launch {
            val usuario = repositorio.autenticar(nombre, clave)
            if (usuario == null) {
                _uiState.value = SesionUiState(error = "Usuario o contraseña incorrectos")
            } else {
                estadoGuardado[CLAVE_USUARIO] = usuario.id
                _uiState.value = SesionUiState(usuario = usuario)
                observar(usuario.id)
            }
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }

    fun cerrarSesion() {
        observacion?.cancel()
        estadoGuardado.remove<Long>(CLAVE_USUARIO)
        _uiState.value = SesionUiState()
    }

    private fun observar(id: Long) {
        observacion?.cancel()
        observacion = viewModelScope.launch {
            repositorio.observarUsuario(id).collect { usuario ->
                _uiState.value = SesionUiState(usuario = usuario)
            }
        }
    }

    companion object {
        private const val CLAVE_USUARIO = "usuarioId"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SolicitudesApp
                SesionViewModel(app.repositorioUsuarios, createSavedStateHandle())
            }
        }
    }
}
