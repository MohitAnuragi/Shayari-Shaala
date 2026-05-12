package com.shayarishaala.shayarishaala.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_shayari",
    indices = [Index(value = ["text"], unique = true)]   // prevents duplicates
)
data class FavoriteShayari(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)
