package com.example.myapplication.repository

import com.example.myapplication.data.model.Post
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitInstance
import java.io.IOException

/** Acceso a los posts. Recibe el ApiService para poder simularlo en las pruebas. */
class PostRepository(private val api: ApiService = RetrofitInstance.api) {

    suspend fun getPosts(): List<Post> = api.getPosts()

    suspend fun createPost(post: Post): Post = api.createPost(post)

    suspend fun updatePost(post: Post): Post = api.updatePost(post.id, post)

    suspend fun deletePost(id: Int) {
        val respuesta = api.deletePost(id)
        if (!respuesta.isSuccessful) throw IOException("Error HTTP ${respuesta.code()}")
    }
}
