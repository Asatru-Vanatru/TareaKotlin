package com.example.myapplication.ui.roles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.local.Rol
import com.example.myapplication.data.local.UsuarioEntity
import com.example.myapplication.data.repository.puedeCambiarRol

/** Pantalla de administrador: cambia a los usuarios entre Cliente y Tecnico. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesScreen(
    viewModel: RolesViewModel,
    actor: UsuarioEntity,
    onVolver: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar roles") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            mensaje?.let {
                Text(it, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usuarios, key = { it.id }) { usuario ->
                    TarjetaUsuario(
                        usuario = usuario,
                        actor = actor,
                        onCambiar = { rol -> viewModel.cambiarRol(actor.id, usuario, rol) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: UsuarioEntity,
    actor: UsuarioEntity,
    onCambiar: (Rol) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)

            if (usuario.protegido) {
                Text("${usuario.rol.etiqueta} (protegido)")
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(Rol.CLIENTE, Rol.TECNICO).forEach { rol ->
                        FilterChip(
                            selected = usuario.rol == rol,
                            onClick = { if (usuario.rol != rol) onCambiar(rol) },
                            enabled = puedeCambiarRol(actor, usuario, rol),
                            label = { Text(rol.etiqueta) }
                        )
                    }
                }
            }
        }
    }
}
