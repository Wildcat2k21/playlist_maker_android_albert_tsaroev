package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.preferences.SearchHistoryAppPreferences
import com.practicum.playlistmaker.domain.api.SearchHistoryAppRepository

class SearchHistoryAppRepositoryImpl(
    private val preferences: SearchHistoryAppPreferences,
) : SearchHistoryAppRepository {

    override suspend fun getHistoryRequests(): List<String> {
        return preferences.getEntries()
    }

    override fun addToHistory(word: WordApp) {
        preferences.addEntry(word = word.word)
    }
}