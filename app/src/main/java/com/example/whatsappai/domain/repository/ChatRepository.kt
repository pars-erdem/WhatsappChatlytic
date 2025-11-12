package com.example.whatsappai.domain.repository

import com.example.whatsappai.domain.model.ChatHistoryItem
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChatHistory(): Flow<List<ChatHistoryItem>>
    suspend fun getChatHistoryById(id: Long): ChatHistoryItem?
    suspend fun uploadAndAnalyze(
        fileName: String,
        fileSize: Long,
        fileContent: ByteArray
    ): Result<ChatHistoryItem>
    suspend fun deleteChatHistory(id: Long)
}

