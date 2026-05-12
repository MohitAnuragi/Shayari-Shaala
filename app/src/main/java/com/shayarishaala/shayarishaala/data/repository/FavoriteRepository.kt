package com.shayarishaala.shayarishaala.data.repository

import com.shayarishaala.shayarishaala.data.db.FavoriteDao
import com.shayarishaala.shayarishaala.data.db.FavoriteShayari
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {

    fun getAllFavorites(): Flow<List<FavoriteShayari>> = dao.getAllFavorites()

    fun isFavorite(text: String): Flow<Boolean> = dao.isFavorite(text)

    suspend fun addFavorite(text: String, category: String = "General") {
        dao.insert(FavoriteShayari(text = text, category = category))
    }

    suspend fun removeFavoriteByText(text: String) {
        val item = dao.getByText(text)
        if (item != null) dao.delete(item)
    }

    suspend fun toggleFavorite(text: String, category: String = "General") {
        val item = dao.getByText(text)
        if (item != null) {
            dao.delete(item)
        } else {
            dao.insert(FavoriteShayari(text = text, category = category))
        }
    }
}
