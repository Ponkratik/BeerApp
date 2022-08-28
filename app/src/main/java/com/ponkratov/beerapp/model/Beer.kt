package com.ponkratov.beerapp.model

import com.google.gson.annotations.SerializedName

data class Beer(
    val id: Int,
    val name: String,
    val tagline: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("brewers_tips")
    val brewersTips: String
)
