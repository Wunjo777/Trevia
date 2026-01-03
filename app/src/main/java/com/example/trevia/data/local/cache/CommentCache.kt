package com.example.trevia.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment_cache")
data class CommentCache(
    @PrimaryKey val poiId: String,
    val commentContent: String,
    val updatedAt: Long,
    val lastAccess:Long
)
