package com.example.myapplication.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton con la configuracion de Retrofit (mismo patron de la guia 14).
 *
 * BASE_URL: 10.0.2.2 es la direccion con la que el emulador de Android ve el
 * "localhost" del PC. Si el backend corre en otro lugar (celular real,
 * servidor en la nube), cambiar SOLO esta constante. Debe terminar en "/".
 */
object RetrofitInstance {

    const val BASE_URL = "http://10.0.2.2:3000/api/"

    private val cliente = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        // Muestra cada peticion y respuesta en Logcat; util para depurar.
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val api: SolicitudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolicitudApiService::class.java)
    }
}
