package com.example.myapplication.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Configuracion de Retrofit. El servicio se crea una sola vez. */
object RetrofitInstance {

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
