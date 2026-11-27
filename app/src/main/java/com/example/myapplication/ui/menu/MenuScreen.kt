package com.example.myapplication.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.myapplication.data.local.Rol

/**
 * Menu principal.
 *
 * El menu de cliente es la base. Tecnico y administrador ven ademas las
 * opciones propias de su rol.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    nombreUsuario: String,
    rol: Rol,
    onIrAPanelTecnico: () -> Unit,
    onIrAAdministrarRoles: () -> Unit,
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("$nombreUsuario · ${rol.etiqueta}")

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

            Button(
                onClick = onIrAHistorial,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Historial")
            }

            if (rol.vePanelTecnico) {
                Button(
                    onClick = onIrAPanelTecnico,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Panel técnico")
                }
            }

            if (rol.administraRoles) {
                Button(
                    onClick = onIrAAdministrarRoles,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Administrar roles")
                }
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
