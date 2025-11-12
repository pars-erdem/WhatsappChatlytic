package com.example.whatsappai.data.local.dao

import androidx.room.*
import com.example.whatsappai.data.local.entity.ChatHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Query("SELECT * FROM chat_history ORDER BY uploadDate DESC")
    fun getAllChatHistory(): Flow<List<ChatHistoryEntity>>

    @Query("SELECT * FROM chat_history WHERE id = :id")
    suspend fun getChatHistoryById(id: Long): ChatHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatHistory(chatHistory: ChatHistoryEntity): Long

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity)

    @Query("DELETE FROM chat_history WHERE id = :id")
    suspend fun deleteChatHistoryById(id: Long)
}

