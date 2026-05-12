package com.shayarishaala.shayarishaala.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteShayari)

    @Delete
    suspend fun delete(favorite: FavoriteShayari)

    @Query("SELECT * FROM favorite_shayari ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteShayari>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_shayari WHERE text = :text)")
    fun isFavorite(text: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_shayari WHERE text = :text LIMIT 1")
    suspend fun getByText(text: String): FavoriteShayari?
}
