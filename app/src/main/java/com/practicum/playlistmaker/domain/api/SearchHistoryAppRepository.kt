package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.network.WordApp

interface SearchHistoryAppRepository {

    suspend fun getHistoryRequests(): List<String>

    fun addToHistory(word: WordApp)
}