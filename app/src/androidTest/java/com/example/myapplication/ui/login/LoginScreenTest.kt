package com.example.myapplication.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun muestraElementosDelLogin() {
        composeTestRule.setContent {
            LoginScreen(onIngresar = {})
        }

        composeTestRule.onNodeWithText("Gestión de Solicitudes")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Usuario")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Contraseña")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Ingresar")
            .assertIsDisplayed()
    }

    @Test
    fun camposVacios_muestraError() {
        composeTestRule.setContent {
            LoginScreen(onIngresar = {})
        }

        composeTestRule.onNodeWithText("Ingresar").performClick()

        composeTestRule.onNodeWithText(
            "Debe completar usuario y contraseña"
        ).assertIsDisplayed()
    }

    @Test
    fun camposCompletos_ejecutaIngreso() {
        var ingresoEjecutado = false

        composeTestRule.setContent {
            LoginScreen(
                onIngresar = { ingresoEjecutado = true }
            )
        }

        composeTestRule.onNodeWithText("Usuario")
            .performTextInput("maira")

        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("12345")
        composeTestRule.onNodeWithText("Ingresar").performClick()

        assertTrue(ingresoEjecutado)
    }
}
