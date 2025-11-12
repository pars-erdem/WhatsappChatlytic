package com.example.whatsappai.data.repository

import com.example.whatsappai.data.local.dao.ChatHistoryDao
import com.example.whatsappai.data.mapper.toDomain
import com.example.whatsappai.data.mapper.toEntity
import com.example.whatsappai.data.remote.api.ChatAnalysisApi
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val chatAnalysisApi: ChatAnalysisApi
) : ChatRepository {

    override fun getAllChatHistory(): Flow<List<ChatHistoryItem>> {
        return chatHistoryDao.getAllChatHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getChatHistoryById(id: Long): ChatHistoryItem? {
        return chatHistoryDao.getChatHistoryById(id)?.toDomain()
    }

    override suspend fun uploadAndAnalyze(
        fileName: String,
        fileSize: Long,
        fileContent: ByteArray
    ): Result<ChatHistoryItem> {
        return try {
            val requestFile = okhttp3.RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                fileContent
            )
            val filePart = okhttp3.MultipartBody.Part.createFormData(
                "file",
                fileName,
                requestFile
            )

            val response = chatAnalysisApi.uploadAndAnalyze(filePart)
            
            if (response.isSuccessful && response.body() != null) {
                val analysisResponse = response.body()!!
                val chatHistoryItem = analysisResponse.toDomain(fileName, fileSize)
                
                // Save to local database
                val entityId = chatHistoryDao.insertChatHistory(chatHistoryItem.toEntity())
                val savedItem = chatHistoryItem.copy(id = entityId)
                
                Result.success(savedItem)
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChatHistory(id: Long) {
        chatHistoryDao.deleteChatHistoryById(id)
    }
}

