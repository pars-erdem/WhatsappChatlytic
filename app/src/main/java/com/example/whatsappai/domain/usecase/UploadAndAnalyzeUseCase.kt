package com.example.whatsappai.domain.usecase

import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.repository.ChatRepository
import javax.inject.Inject

class UploadAndAnalyzeUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        fileName: String,
        fileSize: Long,
        fileContent: ByteArray
    ): Result<ChatHistoryItem> {
        return repository.uploadAndAnalyze(fileName, fileSize, fileContent)
    }
}

