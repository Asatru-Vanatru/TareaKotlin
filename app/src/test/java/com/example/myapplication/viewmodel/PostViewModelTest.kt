package com.example.myapplication.viewmodel

import com.example.myapplication.data.model.Post
import com.example.myapplication.repository.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    val posts = listOf(Post(1, 1, "Titulo 1", "Cuerpo 1"), Post(1, 2, "Titulo 2", "Cuerpo 2"))

    // viewModelScope usa el hilo principal, que no existe en pruebas locales.
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun repositorio(): PostRepository {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns posts
        return repo
    }

    "al iniciar carga los posts del repositorio" {
        val estado = PostViewModel(repositorio()).uiState.value

        estado.posts shouldContainExactly posts
        estado.cargando shouldBe false
    }

    "si falla la carga muestra un mensaje de error" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } throws IOException("sin red")

        val estado = PostViewModel(repo).uiState.value

        estado.posts.shouldBeEmpty()
        estado.mensaje shouldBe "No se pudieron obtener los posts"
        estado.cargando shouldBe false
    }

    "crearPost() agrega el post al inicio con un id unico" {
        val repo = repositorio()
        coEvery { repo.createPost(any()) } returns Post(1, 101, "Nuevo", "Texto")
        val viewModel = PostViewModel(repo)

        viewModel.crearPost("Nuevo", "Texto")
        viewModel.crearPost("Nuevo", "Texto")

        viewModel.uiState.value.posts.map { it.id } shouldContainExactly listOf(102, 101, 1, 2)
    }

    "crearPost() con campos vacios no llama a la API" {
        val repo = repositorio()
        val viewModel = PostViewModel(repo)

        viewModel.crearPost(" ", "Texto")

        coVerify(exactly = 0) { repo.createPost(any()) }
    }

    "editarPost() reemplaza el post en la lista" {
        val repo = repositorio()
        val editado = posts[0].copy(title = "Editado")
        coEvery { repo.updatePost(editado) } returns editado
        val viewModel = PostViewModel(repo)

        viewModel.editarPost(posts[0], "Editado", "Cuerpo 1")

        viewModel.uiState.value.posts shouldContainExactly listOf(editado, posts[1])
    }

    "eliminarPost() quita el post de la lista" {
        val repo = repositorio()
        coEvery { repo.deletePost(1) } returns Unit
        val viewModel = PostViewModel(repo)

        viewModel.eliminarPost(posts[0])

        viewModel.uiState.value.posts shouldContainExactly listOf(posts[1])
    }
})
