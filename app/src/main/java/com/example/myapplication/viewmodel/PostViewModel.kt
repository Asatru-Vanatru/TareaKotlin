package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Post
import com.example.myapplication.repository.PostRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val cargando: Boolean = false,
    val mensaje: String? = null
)

/**
 * Estado y logica de los posts.
 *
 * jsonplaceholder responde a POST, PUT y DELETE pero no guarda los cambios,
 * por eso la lista se actualiza aqui despues de cada respuesta correcta.
 */
class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    /**
     * Posts creados en la app. No existen en el servidor (un PUT sobre ellos
     * falla), asi que se editan y eliminan solo en memoria.
     */
    private val idsLocales = mutableSetOf<Int>()

    init {
        cargarPosts()
    }

    fun cargarPosts() = ejecutar("No se pudieron obtener los posts") {
        val posts = repository.getPosts()
        idsLocales.clear()
        _uiState.update { it.copy(posts = posts) }
    }

    fun crearPost(titulo: String, contenido: String) {
        if (titulo.isBlank() || contenido.isBlank()) {
            mostrarCamposVacios()
            return
        }

        ejecutar("No se pudo crear el post") {
            val creado = repository.createPost(Post(1, 0, titulo.trim(), contenido.trim()))
            // La API entrega siempre el mismo id (101): se asigna uno unico.
            val id = maxOf(creado.id, (_uiState.value.posts.maxOfOrNull { it.id } ?: 0) + 1)
            idsLocales.add(id)
            _uiState.update {
                it.copy(posts = listOf(creado.copy(id = id)) + it.posts, mensaje = "Post creado")
            }
        }
    }

    fun editarPost(post: Post, titulo: String, contenido: String) {
        if (titulo.isBlank() || contenido.isBlank()) {
            mostrarCamposVacios()
            return
        }

        ejecutar("No se pudo actualizar el post") {
            val cambios = post.copy(title = titulo.trim(), body = contenido.trim())
            val actualizado =
                if (post.id in idsLocales) cambios else repository.updatePost(cambios)
            _uiState.update { estado ->
                estado.copy(
                    posts = estado.posts.map { if (it.id == post.id) actualizado else it },
                    mensaje = "Post actualizado"
                )
            }
        }
    }

    fun eliminarPost(post: Post) = ejecutar("No se pudo eliminar el post") {
        if (post.id !in idsLocales) repository.deletePost(post.id)
        _uiState.update { estado ->
            estado.copy(
                posts = estado.posts.filterNot { it.id == post.id },
                mensaje = "Post eliminado"
            )
        }
    }

    private fun mostrarCamposVacios() {
        _uiState.update { it.copy(mensaje = "El titulo y el contenido son obligatorios") }
    }

    private fun ejecutar(mensajeError: String, accion: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }
            try {
                accion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = mensajeError) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
