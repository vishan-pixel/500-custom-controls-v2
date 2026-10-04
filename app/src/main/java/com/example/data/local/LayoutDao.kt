package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LayoutDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE layoutId = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE layoutId = :id")
    suspend fun removeFavorite(id: String)

    @Query("SELECT * FROM custom_layouts ORDER BY createdAt DESC")
    fun getAllCustomLayouts(): Flow<List<CustomLayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomLayout(layout: CustomLayoutEntity)

    @Query("DELETE FROM custom_layouts WHERE id = :id")
    suspend fun deleteCustomLayout(id: String)
}
