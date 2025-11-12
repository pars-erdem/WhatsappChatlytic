package com.example.whatsappai.ui.screen.upload

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.whatsappai.ui.model.UploadState
import com.example.whatsappai.ui.util.FileUtils
import com.example.whatsappai.ui.util.FileValidationResult
import com.example.whatsappai.ui.util.rememberFilePickerLauncher
import com.example.whatsappai.ui.viewmodel.UploadViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onBackClick: () -> Unit,
    onUploadSuccess: (com.example.whatsappai.domain.model.ChatHistoryItem) -> Unit,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uploadState by viewModel.uploadState.collectAsState()
    val selectedFileName by viewModel.selectedFileName.collectAsState()
    val selectedFileSize by viewModel.selectedFileSize.collectAsState()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shouldUpload by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberFilePickerLauncher { uri ->
        if (uri != null) {
            selectedUri = uri
            val validationResult = FileUtils.validateFile(context, uri)
            when (validationResult) {
                is FileValidationResult.Success -> {
                    errorMessage = null
                    viewModel.setSelectedFile(validationResult.fileName, validationResult.fileSize)
                }
                is FileValidationResult.Error -> {
                    errorMessage = validationResult.message
                    viewModel.clearSelectedFile()
                }
            }
        }
    }

    // Handle upload state
    LaunchedEffect(uploadState) {
        when (val state = uploadState) {
            is UploadState.Success -> {
                onUploadSuccess(state.result)
            }
            is UploadState.Error -> {
                errorMessage = state.message
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload File") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uploadState) {
                is UploadState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing...")
                }
                else -> {
                    // File Selection Button
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            filePickerLauncher.launch("text/plain")
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedFileName ?: "Choose a file (.txt only)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // File Info
                    if (selectedFileName != null && selectedFileSize != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "File: $selectedFileName",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Size: ${formatFileSize(selectedFileSize!!)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Error Message
                    errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Upload Button
                    if (selectedFileName != null && selectedFileSize != null && selectedUri != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                shouldUpload = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload & Analyze")
                        }
                    }
                }
            }
        }
    }
    
    // Handle file reading and upload in a coroutine
    LaunchedEffect(shouldUpload) {
        if (shouldUpload && selectedUri != null && selectedFileName != null && selectedFileSize != null) {
            shouldUpload = false
            val fileContent = FileUtils.readFileContent(context, selectedUri!!)
            if (fileContent != null) {
                viewModel.uploadAndAnalyze(fileContent)
            } else {
                errorMessage = "Could not read file content"
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    val df = DecimalFormat("#.##")
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${df.format(bytes / 1024.0)} KB"
        else -> "${df.format(bytes / (1024.0 * 1024.0))} MB"
    }
}

