package com.example.myapplication.ui.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.SolicitudesApp
import com.example.myapplication.data.local.SolicitudEntity
import com.example.myapplication.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel del modulo de Solicitudes.
 *
 * Concentra TODA la logica: validaciones, armado del estado y guardado.
 * Las pantallas solo dibujan lo que este ViewModel expone y le reportan
 * los eventos del usuario. Esa es la regla que la pauta marca como
 * "MVVM real, no solamente carpetas llamadas MVVM".
 *
 * El ViewModel sobrevive a los giros de pantalla, por eso el formulario
 * no se borra al rotar el telefono.
 */
class SolicitudViewModel(
    private val repositorio: SolicitudRepository
) : ViewModel() {

    /**
     * Lista de solicitudes guardadas. Viene directo de Room como Flow y se
     * convierte en StateFlow para que Compose la observe.
     * Cuando se inserta una solicitud, Room emite la lista nueva y la
     * pantalla se actualiza sola: eso es la reactividad que pide la pauta.
     */
    val solicitudes: StateFlow<List<SolicitudEntity>> =
        repositorio.solicitudes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Estado del formulario. Privado para escribir, publico solo de lectura. */
    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    // ------------------------------------------------------------------
    // Eventos que llegan desde la pantalla
    // ------------------------------------------------------------------

    fun onTituloChange(valor: String) {
        // Al escribir se limpia el error de ese campo: el mensaje desaparece
        // apenas el usuario corrige, sin esperar a que vuelva a guardar.
        _uiState.update { it.copy(titulo = valor, tituloError = null) }
    }

    fun onDescripcionChange(valor: String) {
        _uiState.update { it.copy(descripcion = valor, descripcionError = null) }
    }

    fun onCategoriaChange(valor: String) {
        _uiState.update { it.copy(categoria = valor, categoriaError = null) }
    }

    fun onServicioChange(valor: String) {
        _uiState.update { it.copy(servicio = valor, servicioError = null) }
    }

    /** Guarda la ruta de la foto de evidencia tomada con la camara. */
    fun onEvidenciaCapturada(uri: String) {
        _uiState.update { it.copy(evidenciaUri = uri) }
    }

    // ------------------------------------------------------------------
    // Carga de una solicitud existente para editarla
    // ------------------------------------------------------------------

    fun cargarSolicitud(id: Long) {
        if (id == 0L) {
            // Formulario nuevo: se parte de un estado limpio.
            _uiState.value = SolicitudUiState()
            return
        }
        viewModelScope.launch {
            repositorio.obtenerPorId(id)?.let { solicitud ->
                _uiState.value = SolicitudUiState(
                    id = solicitud.id,
                    codigo = solicitud.codigo,
                    titulo = solicitud.titulo,
                    descripcion = solicitud.descripcion,
                    categoria = solicitud.categoria,
                    servicio = solicitud.servicio,
                    evidenciaUri = solicitud.evidenciaUri,
                    estado = solicitud.estado
                )
            }
        }
    }

    // ------------------------------------------------------------------
    // Validacion
    // ------------------------------------------------------------------

    /**
     * Revisa los campos obligatorios y escribe los mensajes de error en el
     * estado. Devuelve true solo si no quedo ningun error.
     *
     * La validacion vive aqui y no en la pantalla: la pantalla se limita a
     * mostrar el mensaje que encuentre en el estado.
     */
    private fun validar(): Boolean {
        val estado = _uiState.value

        val errorTitulo = if (estado.titulo.isBlank()) "El titulo es obligatorio" else null
        val errorDescripcion = if (estado.descripcion.isBlank()) "La descripcion es obligatoria" else null
        val errorCategoria = if (estado.categoria.isBlank()) "Debe seleccionar una categoria" else null

        val errorServicio = if (
            estado.categoria in CATEGORIAS_CON_SERVICIO && estado.servicio.isBlank()
        ) {
            "El servicio relacionado es obligatorio para esta categoria"
        } else {
            null
        }

        _uiState.update {
            it.copy(
                tituloError = errorTitulo,
                descripcionError = errorDescripcion,
                categoriaError = errorCategoria,
                servicioError = errorServicio
            )
        }

        return errorTitulo == null &&
                errorDescripcion == null &&
                errorCategoria == null &&
                errorServicio == null
    }

    // ------------------------------------------------------------------
    // Guardado
    // ------------------------------------------------------------------

    fun guardar() {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }

            val estado = _uiState.value

            val solicitud = SolicitudEntity(
                id = estado.id,
                // Una solicitud nueva recibe un codigo unico generado aqui.
                // Una existente conserva el suyo.
                codigo = estado.codigo.ifBlank { generarCodigo() },
                titulo = estado.titulo.trim(),
                descripcion = estado.descripcion.trim(),
                categoria = estado.categoria,
                servicio = estado.servicio.trim(),
                evidenciaUri = estado.evidenciaUri,
                estado = estado.estado
            )

            repositorio.guardar(solicitud)

            _uiState.update {
                it.copy(guardando = false, guardadoExitosamente = true)
            }
        }
    }

    /** Se llama despues de navegar de vuelta, para no repetir la navegacion. */
    fun consumirGuardado() {
        _uiState.update { it.copy(guardadoExitosamente = false) }
    }

    fun eliminar(solicitud: SolicitudEntity) {
        viewModelScope.launch { repositorio.eliminar(solicitud) }
    }

    /** Identificador unico visible, del tipo "SOL-3F9A2B11". */
    private fun generarCodigo(): String =
        "SOL-" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()

    // ------------------------------------------------------------------
    // Fabrica: entrega el repositorio al ViewModel al momento de crearlo
    // ------------------------------------------------------------------

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SolicitudesApp
                SolicitudViewModel(app.repositorio)
            }
        }
    }
}
