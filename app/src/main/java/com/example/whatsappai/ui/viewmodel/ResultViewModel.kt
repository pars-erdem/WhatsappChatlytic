package com.example.whatsappai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.repository.ChatRepository
import com.example.whatsappai.domain.usecase.DeleteChatHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val deleteChatHistoryUseCase: DeleteChatHistoryUseCase,
    private val repository: ChatRepository
) : ViewModel() {

    private val _chatHistoryItem = MutableStateFlow<ChatHistoryItem?>(null)
    val chatHistoryItem: StateFlow<ChatHistoryItem?> = _chatHistoryItem.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadChatHistoryItem(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _chatHistoryItem.value = repository.getChatHistoryById(id)
            _isLoading.value = false
        }
    }

    fun deleteAnalysis() {
        val item = _chatHistoryItem.value ?: return
        viewModelScope.launch {
            deleteChatHistoryUseCase(item.id)
        }
    }
}

