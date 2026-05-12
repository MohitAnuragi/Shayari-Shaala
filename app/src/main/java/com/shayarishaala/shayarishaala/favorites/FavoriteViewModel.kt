package com.shayarishaala.shayarishaala.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shayarishaala.shayarishaala.data.db.FavoriteShayari
import com.shayarishaala.shayarishaala.data.db.ShayariDatabase
import com.shayarishaala.shayarishaala.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteRepository by lazy {
        val dao = ShayariDatabase.getDatabase(application).favoriteDao()
        FavoriteRepository(dao)
    }

    /** Live list of all saved favorites, updates whenever DB changes */
    val favoriteList: StateFlow<List<FavoriteShayari>> =
        repository.getAllFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Toggle – saves if not saved, removes if already saved */
    fun toggleFavorite(text: String, category: String = "General") {
        viewModelScope.launch {
            repository.toggleFavorite(text, category)
        }
    }

    /** Returns a Flow<Boolean> so each card can independently observe its own state */
    fun isFavorite(text: String): Flow<Boolean> = repository.isFavorite(text)

    /** Remove a specific favorite directly (used from FavoritesScreen) */
    fun removeFavorite(text: String) {
        viewModelScope.launch {
            repository.removeFavoriteByText(text)
        }
    }
}
