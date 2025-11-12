package com.example.whatsappai.domain.usecase

import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllChatHistoryUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<ChatHistoryItem>> {
        return repository.getAllChatHistory()
    }
}

