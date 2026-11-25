
package com.example.myapplication.ui.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun usuarioVacio_muestraError() {
        val resultado = validarCamposLogin("", "12345")

        assertEquals(
            "Debe completar usuario y contraseña",
            resultado
        )
    }

    @Test
    fun claveVacia_muestraError() {
        val resultado = validarCamposLogin("maira", "")

        assertEquals(
            "Debe completar usuario y contraseña",
            resultado
        )
    }

    @Test
    fun ambosCamposVacios_muestraError() {
        val resultado = validarCamposLogin("", "")

        assertEquals(
            "Debe completar usuario y contraseña",
            resultado
        )
    }

    @Test
    fun ambosCamposCompletos_noMuestraError() {
        val resultado = validarCamposLogin("maira", "12345")

        assertNull(resultado)
    }

    @Test
    fun usuarioConEspacios_muestraError() {
        val resultado = validarCamposLogin("   ", "12345")

        assertEquals(
            "Debe completar usuario y contraseña",
            resultado
        )
    }
}
