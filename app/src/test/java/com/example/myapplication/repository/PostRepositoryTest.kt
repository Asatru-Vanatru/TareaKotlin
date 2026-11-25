package com.example.myapplication.repository

import com.example.myapplication.data.model.Post
import com.example.myapplication.data.remote.ApiService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class PostRepositoryTest : StringSpec({

    "getPosts() retorna la lista que entrega la API" {
        val posts = listOf(Post(1, 1, "Titulo 1", "Cuerpo 1"), Post(2, 2, "Titulo 2", "Cuerpo 2"))
        val api = mockk<ApiService>()
        coEvery { api.getPosts() } returns posts

        PostRepository(api).getPosts() shouldContainExactly posts
    }

    "updatePost() envia el id del post a la API" {
        val post = Post(1, 7, "Titulo", "Cuerpo")
        val api = mockk<ApiService>()
        coEvery { api.updatePost(7, post) } returns post

        PostRepository(api).updatePost(post)

        coVerify(exactly = 1) { api.updatePost(7, post) }
    }

    "deletePost() falla si la API responde con error" {
        val api = mockk<ApiService>()
        coEvery { api.deletePost(1) } returns Response.error(500, "".toResponseBody())

        shouldThrow<IOException> { PostRepository(api).deletePost(1) }
    }
})
