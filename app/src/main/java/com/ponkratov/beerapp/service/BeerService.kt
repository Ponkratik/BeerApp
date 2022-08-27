package com.ponkratov.beerapp.service

import com.ponkratov.beerapp.dao.BeerApi
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object BeerService {

    private const val BASE_URL = "https://api.punkapi.com/v2/"

    private val retrofit by lazy(LazyThreadSafetyMode.NONE) { provideRetrofit() }
    val beerApi by lazy(LazyThreadSafetyMode.NONE) {
        retrofit.create<BeerApi>()
    }

    private fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}