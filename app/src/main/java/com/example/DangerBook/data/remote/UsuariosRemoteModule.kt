package com.example.DangerBook.data.remote

import android.R.attr.level
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object UsuariosRemoteModule {
    //url de la api rest a consumir
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/" //Poner URL del microservicio

    //interceptar el http
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //cliente
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()


    //consumo de la API
    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //funcion para la implementacion para la interfaz API
    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
