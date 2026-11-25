package com.example.myapplication.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.solicitud.SolicitudViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: SolicitudViewModel,
    onVolver: () -> Unit
) {
    val historial by viewModel.historial.collectAsState()

    // Scaffold pinta el fondo del tema y respeta las barras del sistema,
    // igual que en el resto de las pantallas.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
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
                .padding(horizontal = 16.dp)
        ) {
            if (historial.isEmpty()) {
                Text("No hay movimientos registrados")
            }else{
                LazyColumn{
                    items(historial) { movimiento ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Solicitud N° ${movimiento.solicitudId}")
                                Text(movimiento.comentario)
                                Text("Estado anterior: ${movimiento.estadoAnterior}")
                                Text("Estado nuevo: ${movimiento.estadoNuevo}")

                                val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                val fechaFormateada = formato.format(Date(movimiento.fecha))

                                Text("Fecha: $fechaFormateada")
                            }
                        }
                    }
                }
            }
        }
    }
}