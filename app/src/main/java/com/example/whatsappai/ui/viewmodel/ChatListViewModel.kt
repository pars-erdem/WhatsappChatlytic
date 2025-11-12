package com.example.whatsappai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.usecase.DeleteChatHistoryUseCase
import com.example.whatsappai.domain.usecase.GetAllChatHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getAllChatHistoryUseCase: GetAllChatHistoryUseCase,
    private val deleteChatHistoryUseCase: DeleteChatHistoryUseCase
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatHistoryItem>>(emptyList())
    val chatHistory: StateFlow<List<ChatHistoryItem>> = _chatHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            getAllChatHistoryUseCase().collect { history ->
                _chatHistory.value = history
            }
        }
    }

    fun deleteChatHistory(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteChatHistoryUseCase(id)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}

