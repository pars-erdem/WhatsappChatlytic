package com.example.whatsappai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.usecase.UploadAndAnalyzeUseCase
import com.example.whatsappai.ui.model.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadAndAnalyzeUseCase: UploadAndAnalyzeUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName.asStateFlow()

    private val _selectedFileSize = MutableStateFlow<Long?>(null)
    val selectedFileSize: StateFlow<Long?> = _selectedFileSize.asStateFlow()

    fun setSelectedFile(fileName: String, fileSize: Long) {
        _selectedFileName.value = fileName
        _selectedFileSize.value = fileSize
    }

    fun clearSelectedFile() {
        _selectedFileName.value = null
        _selectedFileSize.value = null
        _uploadState.value = UploadState.Idle
    }

    fun uploadAndAnalyze(fileContent: ByteArray) {
        val fileName = _selectedFileName.value
        val fileSize = _selectedFileSize.value

        if (fileName == null || fileSize == null) {
            _uploadState.value = UploadState.Error("No file selected")
            return
        }

        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            uploadAndAnalyzeUseCase(fileName, fileSize, fileContent)
                .onSuccess { result ->
                    _uploadState.value = UploadState.Success(result)
                }
                .onFailure { exception ->
                    _uploadState.value = UploadState.Error(exception.message ?: "Upload failed")
                }
        }
    }
}

