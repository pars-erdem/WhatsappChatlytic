package com.example.whatsappai.ui.model

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val result: com.example.whatsappai.domain.model.ChatHistoryItem) : UploadState()
    data class Error(val message: String) : UploadState()
}

