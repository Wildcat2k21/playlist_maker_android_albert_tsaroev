package com.practicum.playlistmaker.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creator.DependencyProvider
import com.practicum.playlistmaker.data.network.WordApp
import com.practicum.playlistmaker.domain.api.SearchHistoryAppRepository
import com.practicum.playlistmaker.domain.api.TracksAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

@OptIn(FlowPreview::class)
class SearchAppViewModel(
    private val tracksRepository: TracksAppRepository,
    private val searchHistoryRepository: SearchHistoryAppRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _searchAppScreenState = MutableStateFlow<SearchAppState>(SearchAppState.Initial)
    val searchAppScreenState = _searchAppScreenState.asStateFlow()

    private val searchGeneration = AtomicInteger(0)

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(1000)
                .distinctUntilChanged()
                .collect { query ->
                    val trimmed = query.trim()
                    if (trimmed.isNotEmpty()) {
                        performSearch(trimmed)
                    }
                }
        }
    }

    companion object {
        fun appFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchAppViewModel(
                        tracksRepository = DependencyProvider.provideTracksRepository(context),
                        searchHistoryRepository = DependencyProvider.provideSearchHistoryRepository(context),
                    ) as T
                }
            }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchNow() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty()) {
            performSearch(query)
        }
    }

    fun clearSearch() {
        searchGeneration.incrementAndGet()
        _searchQuery.value = ""
        _searchAppScreenState.value = SearchAppState.Initial
    }

    fun retryLastSearch() {
        val state = _searchAppScreenState.value
        if (state is SearchAppState.Fail) {
            performSearch(state.lastQuery)
        }
    }

    private fun performSearch(trimmedQuery: String) {
        if (trimmedQuery.isEmpty()) return
        val generation = searchGeneration.incrementAndGet()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchAppScreenState.update { SearchAppState.Searching }
                searchHistoryRepository.addToHistory(WordApp(word = trimmedQuery))
                val list = tracksRepository.searchTracks(trimmedQuery)
                if (generation != searchGeneration.get()) return@launch
                _searchAppScreenState.update { SearchAppState.Success(foundList = list) }
            } catch (e: IOException) {
                if (generation != searchGeneration.get()) return@launch
                val message = e.message?.trim().orEmpty().ifEmpty { "Network error" }
                _searchAppScreenState.update {
                    SearchAppState.Fail(error = message, lastQuery = trimmedQuery)
                }
            }
        }
    }

    suspend fun getHistoryList() = searchHistoryRepository.getHistoryRequests()
}