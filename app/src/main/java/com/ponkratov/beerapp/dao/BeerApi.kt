package com.ponkratov.beerapp.dao

import com.ponkratov.beerapp.model.Beer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BeerApi {
    @GET("beers")
    fun getBeers(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Beer>>
}