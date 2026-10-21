package com.example.myapplication.ui.solicitud

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myapplication.util.crearUriImagenTemporal

/**
 * Pantalla 2: formulario para crear o editar una solicitud.
 *
 * Cada campo lee su valor del UiState y avisa al ViewModel cuando cambia.
 * Los errores no se calculan aqui: se muestran los que el ViewModel dejo
 * escritos en el estado, usando isError y supportingText como indica la pauta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    viewModel: SolicitudViewModel,
    solicitudId: Long,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Carga la solicitud al entrar. LaunchedEffect con clave solicitudId hace
    // que esto corra una sola vez por solicitud, no en cada redibujo.
    LaunchedEffect(solicitudId) {
        viewModel.cargarSolicitud(solicitudId)
    }

    // Cuando el guardado termina bien, se navega de vuelta a la lista.
    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.consumirGuardado()
            onGuardado()
        }
    }

    // --- Camara: recurso nativo del dispositivo ---
    // Se guarda la Uri de destino antes de abrir la camara, porque el
    // contrato TakePicture escribe la foto en esa ruta y solo devuelve
    // true/false.
    var uriPendiente by remember { mutableStateOf<Uri?>(null) }

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            uriPendiente?.let { viewModel.onEvidenciaCapturada(it.toString()) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (solicitudId == 0L) "Nueva solicitud" else "Editar solicitud")
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (estado.codigo.isNotBlank()) {
                Text(
                    text = "Codigo: ${estado.codigo}   ·   Estado: ${estado.estado}",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // --- Titulo ---
            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Titulo") },
                isError = estado.tituloError != null,
                supportingText = { Text(estado.tituloError ?: "") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Descripcion ---
            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripcion") },
                isError = estado.descripcionError != null,
                supportingText = { Text(estado.descripcionError ?: "") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Categoria (lista desplegable) ---
            var menuAbierto by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = menuAbierto,
                onExpandedChange = { menuAbierto = it }
            ) {
                OutlinedTextField(
                    value = estado.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    isError = estado.categoriaError != null,
                    supportingText = { Text(estado.categoriaError ?: "") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    CATEGORIAS.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                viewModel.onCategoriaChange(categoria)
                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            // --- Servicio relacionado ---
            OutlinedTextField(
                value = estado.servicio,
                onValueChange = viewModel::onServicioChange,
                label = {
                    Text(
                        if (estado.categoria in CATEGORIAS_CON_SERVICIO)
                            "Servicio relacionado"
                        else
                            "Servicio relacionado (opcional)"
                    )
                },
                isError = estado.servicioError != null,
                supportingText = { Text(estado.servicioError ?: "") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Evidencia fotografica ficticia ---
            Text("Evidencia fotografica", style = MaterialTheme.typography.titleSmall)

            estado.evidenciaUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Evidencia de la solicitud",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Spacer(Modifier.height(4.dp))
            }

            OutlinedButton(
                onClick = {
                    val uri = crearUriImagenTemporal(context)
                    uriPendiente = uri
                    lanzadorCamara.launch(uri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text(
                    text = if (estado.evidenciaUri == null)
                        "  Tomar fotografia"
                    else
                        "  Reemplazar fotografia"
                )
            }

            Spacer(Modifier.height(8.dp))

            // --- Guardar ---
            Button(
                onClick = viewModel::guardar,
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar solicitud")
                }
            }
        }
    }
}
