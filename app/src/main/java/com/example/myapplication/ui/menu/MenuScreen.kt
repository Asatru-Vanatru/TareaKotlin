package com.example.myapplication.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Menu principal.
 *
 * Punto de reparto hacia los modulos del caso. En esta primera entrega solo
 * el modulo de Solicitudes esta desarrollado; el resto queda visible pero
 * desactivado para dejar claro el alcance entregado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onIrASolicitudes: () -> Unit,
    onIrAPosts: () -> Unit,
    onIrAHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu principal") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Modulos disponibles",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = onIrASolicitudes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitudes")
            }

            Button(
                onClick = onIrAPosts,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Posts (API REST)")
            }

            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seguimiento (proxima entrega)")
            }

            Button(
                onClick = onIrAHistorial,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Historial")
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesion")
            }
        }
    }
}
