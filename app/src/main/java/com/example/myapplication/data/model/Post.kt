package com.example.myapplication.data.model

/** Un post obtenido desde la API. */
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
