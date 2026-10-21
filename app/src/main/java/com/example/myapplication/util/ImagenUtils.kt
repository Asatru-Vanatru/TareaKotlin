package com.example.myapplication.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Crea un archivo vacio dentro del almacenamiento privado de la app y
 * devuelve su Uri a traves de FileProvider.
 *
 * Android no permite entregarle a la camara una ruta directa (file://) por
 * seguridad. FileProvider genera una Uri de contenido (content://) con
 * permiso temporal, que es lo unico que otra app puede recibir.
 */
fun crearUriImagenTemporal(context: Context): Uri {
    val marcaDeTiempo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        .format(Date())

    val carpeta = File(context.filesDir, "evidencias").apply { mkdirs() }
    val archivo = File(carpeta, "evidencia_$marcaDeTiempo.jpg")

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}
