package com.example.myapplication.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.myapplication.data.model.Post
import com.example.myapplication.ui.screens.PostContent
import com.example.myapplication.viewmodel.PostUiState
import org.junit.Rule
import org.junit.Test

class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_post_debe_aparecer_en_pantalla() {
        val posts = listOf(
            Post(1, 1, "Titulo 1", "Contenido 1"),
            Post(2, 2, "Titulo 2", "Contenido 2")
        )

        composeRule.setContent { PostContent(estado = PostUiState(posts = posts)) }

        composeRule.onNodeWithText("Titulo: Titulo 1").assertIsDisplayed()
        composeRule.onNodeWithText("Titulo: Titulo 2").assertIsDisplayed()
    }
}
