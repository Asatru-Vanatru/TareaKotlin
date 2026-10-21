package com.example.myapplication.ui.solicitud

/**
 * Estado completo de la pantalla de formulario.
 *
 * Toda la informacion que la pantalla necesita para dibujarse vive aqui:
 * los valores escritos, los mensajes de error y las banderas de proceso.
 * La pantalla no guarda estado propio, solo lee este objeto y avisa al
 * ViewModel cuando el usuario hace algo.
 *
 * Es inmutable: cada cambio produce una copia nueva con copy(). Eso permite
 * que Compose detecte la diferencia y redibuje solo lo necesario.
 */
data class SolicitudUiState(
    val id: Long = 0,
    val codigo: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val servicio: String = "",
    val evidenciaUri: String? = null,
    val estado: String = "Recibida",

    // Mensajes de error por campo. null = el campo esta correcto.
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val categoriaError: String? = null,
    val servicioError: String? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)

/** Categorias disponibles en el formulario. */
val CATEGORIAS = listOf(
    "Soporte tecnico",
    "Instalacion",
    "Reclamo",
    "Consulta comercial"
)

/**
 * Categorias en las que el campo "servicio relacionado" pasa a ser
 * obligatorio. La pauta pide que ese campo sea obligatorio "cuando
 * corresponda": aqui queda definido cuando corresponde.
 */
val CATEGORIAS_CON_SERVICIO = listOf(
    "Soporte tecnico",
    "Instalacion",
    "Reclamo"
)
