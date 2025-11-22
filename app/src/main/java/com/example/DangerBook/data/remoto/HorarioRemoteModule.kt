package com.example.DangerBook.data.remoto

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HorarioRemoteModule {
    //La URL va aser la misma por ahora
    //Hasta crear los puertos
    private const val BASE_URL= "https://3hvtx1tb-8080.brs.devtunnels.ms/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY

    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor (logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}