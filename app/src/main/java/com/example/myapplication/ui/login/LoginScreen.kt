
package com.example.myapplication.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Pantalla de ingreso.
 * Valida que los campos no estén vacíos y entrega las credenciales; quien la
 * usa las comprueba contra la base local y devuelve el error si no coinciden.
 */
@Composable
fun LoginScreen(
    onIngresar: (usuario: String, clave: String) -> Unit,
    errorIngreso: String? = null,
    ingresando: Boolean = false,
    onEditar: () -> Unit = {}
) {

    var usuario by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var errorCampos by rememberSaveable { mutableStateOf<String?>(null) }
    val error = errorCampos ?: errorIngreso

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Gestión de Solicitudes",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    errorCampos = null
                    onEditar()
                },
                label = { Text("Usuario") },
                isError = error != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = {
                    clave = it
                    errorCampos = null
                    onEditar()
                },
                label = { Text("Contraseña") },
                isError = error != null,
                supportingText = {
                    if (error != null) {
                        Text(error)
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))


            Button(
                onClick = {
                    errorCampos = validarCamposLogin(usuario, clave)

                    if (errorCampos == null) {
                        onIngresar(usuario, clave)
                    }
                },
                enabled = !ingresando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

        }
    }
}
