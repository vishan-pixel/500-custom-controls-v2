package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val layoutId: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_layouts")
data class CustomLayoutEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val fileName: String,
    val category: String,
    val description: String,
    val jsonContent: String,
    val createdAt: Long = System.currentTimeMillis()
)
