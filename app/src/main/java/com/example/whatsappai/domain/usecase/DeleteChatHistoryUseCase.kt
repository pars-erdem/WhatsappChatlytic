package com.example.whatsappai.domain.usecase

import com.example.whatsappai.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteChatHistoryUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteChatHistory(id)
    }
}

