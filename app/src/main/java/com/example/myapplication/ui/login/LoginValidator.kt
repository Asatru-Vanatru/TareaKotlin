
package com.example.myapplication.ui.login

fun validarCamposLogin(usuario: String, clave: String): String? {

    return if (usuario.isBlank() || clave.isBlank()) {
        "Debe completar usuario y contraseña"
    } else {
        null
    }
}
