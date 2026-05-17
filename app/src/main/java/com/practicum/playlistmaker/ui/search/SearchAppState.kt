package com.practicum.playlistmaker.ui.search

import com.practicum.playlistmaker.data.network.TrackApp

sealed class SearchAppState {
    data object Initial : SearchAppState()
    data object Searching : SearchAppState()
    data class Success(val foundList: List<TrackApp>) : SearchAppState()
    data class Fail(val error: String, val lastQuery: String) : SearchAppState()
}