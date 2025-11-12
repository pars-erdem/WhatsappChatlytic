package com.example.whatsappai.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.whatsappai.data.local.dao.ChatHistoryDao
import com.example.whatsappai.data.local.entity.ChatHistoryEntity

@Database(
    entities = [ChatHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao
}

