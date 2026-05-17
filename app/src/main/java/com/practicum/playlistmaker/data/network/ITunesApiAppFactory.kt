package com.practicum.playlistmaker.data.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ITunesApiAppFactory {
    private val gson = GsonBuilder().create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: ITunesApiAppService = retrofit.create(ITunesApiAppService::class.java)

    fun networkClient(): RetrofitNetworkAppClient = RetrofitNetworkAppClient(api)
}