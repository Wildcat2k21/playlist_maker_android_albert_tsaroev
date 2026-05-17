package com.practicum.playlistmaker.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val MAX_ENTRIES = 10

class SearchHistoryAppPreferences(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson = Gson(),
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("search-history-preferences") + SupervisorJob(),
    ),
) {
    private val preferencesKey = stringPreferencesKey("search_history")
    private val entriesType = object : TypeToken<List<String>>() {}.type

    fun addEntry(word: String) {
        val normalizedWord = word.trim()
        if (normalizedWord.isEmpty()) {
            return
        }

        coroutineScope.launch {
            dataStore.edit { preferences ->
                val history = decodeEntries(preferences[preferencesKey]).toMutableList()

                history.remove(normalizedWord)
                history.add(0, normalizedWord)

                val updatedString = gson.toJson(history.take(MAX_ENTRIES))

                preferences[preferencesKey] = updatedString
            }
        }
    }

    suspend fun getEntries(): List<String> {
        return decodeEntries(dataStore.data.firstOrNull()?.get(preferencesKey))
    }

    private fun decodeEntries(rawValue: String?): List<String> {
        if (rawValue.isNullOrBlank()) {
            return emptyList()
        }
        val parsed = runCatching {
            gson.fromJson<List<String>>(rawValue, entriesType).orEmpty()
        }.getOrDefault(emptyList())
        return parsed
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .take(MAX_ENTRIES)
    }
}