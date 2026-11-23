package com.example.myapplication.ui.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.local.SolicitudEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla 1: listado de solicitudes guardadas.
 *
 * Es una pantalla "tonta" a proposito: no calcula nada, solo recibe la lista
 * desde el ViewModel y la dibuja. Todo lo que el usuario hace se devuelve
 * hacia arriba mediante las funciones que llegan por parametro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSolicitudesScreen(
    viewModel: SolicitudViewModel,
    onNuevaSolicitud: () -> Unit,
    onEditarSolicitud: (Long) -> Unit,
    onVolver: () -> Unit
) {
    // collectAsStateWithLifecycle observa el StateFlow solo mientras la
    // pantalla esta visible, para no gastar recursos en segundo plano.
    val solicitudes by viewModel.solicitudes.collectAsStateWithLifecycle()

    // Solicitud que el usuario quiere borrar; null = ningun dialogo abierto.
    var solicitudAEliminar by remember { mutableStateOf<SolicitudEntity?>(null) }

    solicitudAEliminar?.let { solicitud ->
        AlertDialog(
            onDismissRequest = { solicitudAEliminar = null },
            title = { Text("Eliminar solicitud") },
            text = {
                Text("Se eliminara ${solicitud.codigo} (${solicitud.titulo}). Esta accion no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(solicitud)
                    solicitudAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { solicitudAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaSolicitud) {
                Icon(Icons.Default.Add, contentDescription = "Nueva solicitud")
            }
        }
    ) { padding ->

        if (solicitudes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay solicitudes registradas",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Use el boton + para registrar la primera.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitudes, key = { it.id }) { solicitud ->
                    TarjetaSolicitud(
                        solicitud = solicitud,
                        onClick = { onEditarSolicitud(solicitud.id) },
                        onEliminar = { solicitudAEliminar = solicitud }
                    )
                }
            }
        }
    }
}

/** Tarjeta con los datos que la pauta pide ver: codigo, titulo, categoria y estado. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaSolicitud(
    solicitud: SolicitudEntity,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = solicitud.codigo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = solicitud.estado,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = solicitud.titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = solicitud.categoria,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formato.format(Date(solicitud.fechaCreacion)),
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar solicitud",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
