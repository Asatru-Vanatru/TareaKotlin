package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.model.Post
import com.example.myapplication.viewmodel.PostUiState
import com.example.myapplication.viewmodel.PostViewModel

/** Conecta la pantalla con el ViewModel. */
@Composable
fun PostScreen(viewModel: PostViewModel, onVolver: () -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    PostContent(
        estado = estado,
        onVolver = onVolver,
        onRecargar = { viewModel.cargarPosts() },
        onCrear = viewModel::crearPost,
        onEditar = viewModel::editarPost,
        onEliminar = { viewModel.eliminarPost(it) }
    )
}

/** Solo dibuja el estado recibido, lo que permite probarla sin red. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostContent(
    estado: PostUiState,
    onVolver: () -> Unit = {},
    onRecargar: () -> Unit = {},
    onCrear: (String, String) -> Unit = { _, _ -> },
    onEditar: (Post, String, String) -> Unit = { _, _, _ -> },
    onEliminar: (Post) -> Unit = {}
) {
    var creando by rememberSaveable { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Post?>(null) }
    val estadoLista = rememberLazyListState()

    // Un post nuevo entra al inicio: se sube la lista para que quede a la vista.
    val primerId = estado.posts.firstOrNull()?.id
    LaunchedEffect(primerId) {
        if (estado.mensaje == "Post creado") estadoLista.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Posts") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = onRecargar, enabled = !estado.cargando) {
                        Text("Recargar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())

            estado.mensaje?.let {
                Text(it, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            Button(
                onClick = { creando = true },
                enabled = !estado.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Nuevo post")
            }

            LazyColumn(
                state = estadoLista,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.posts, key = { it.id }) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = "Titulo: ${post.title}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = post.body, style = MaterialTheme.typography.bodyMedium)
                            Row {
                                TextButton(
                                    onClick = { enEdicion = post },
                                    enabled = !estado.cargando
                                ) { Text("Editar") }
                                TextButton(
                                    onClick = { onEliminar(post) },
                                    enabled = !estado.cargando
                                ) { Text("Eliminar") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (creando) {
        DialogoPost(
            encabezado = "Nuevo post",
            onCancelar = { creando = false },
            onGuardar = { titulo, contenido ->
                onCrear(titulo, contenido)
                creando = false
            }
        )
    }

    enEdicion?.let { post ->
        DialogoPost(
            encabezado = "Editar post",
            tituloInicial = post.title,
            contenidoInicial = post.body,
            onCancelar = { enEdicion = null },
            onGuardar = { titulo, contenido ->
                onEditar(post, titulo, contenido)
                enEdicion = null
            }
        )
    }
}

@Composable
private fun DialogoPost(
    encabezado: String,
    tituloInicial: String = "",
    contenidoInicial: String = "",
    onCancelar: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(tituloInicial) }
    var contenido by rememberSaveable { mutableStateOf(contenidoInicial) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(encabezado) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(titulo, { titulo = it }, label = { Text("Titulo") })
                OutlinedTextField(contenido, { contenido = it }, label = { Text("Contenido") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(titulo, contenido) },
                enabled = titulo.isNotBlank() && contenido.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
