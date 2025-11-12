package com.example.whatsappai.ui.screen.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.ui.model.UploadState
import com.example.whatsappai.ui.util.FileUtils
import com.example.whatsappai.ui.util.FileValidationResult
import com.example.whatsappai.ui.util.rememberFilePickerLauncher
import com.example.whatsappai.ui.viewmodel.ChatListViewModel
import com.example.whatsappai.ui.viewmodel.UploadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onNewAnalysisClick: () -> Unit,
    onChatItemClick: (ChatHistoryItem) -> Unit,
    navController: NavHostController,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth >= 600.dp
    
    // Drawer width based on screen size
    val drawerWidth = if (isTablet) {
        (screenWidth * 0.3f).coerceAtMost(320.dp)
    } else {
        (screenWidth * 0.85f).coerceAtMost(320.dp)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatHistoryDrawer(
                chatHistory = chatHistory,
                isLoading = isLoading,
                onNewChatClick = {
                    scope.launch {
                        drawerState.close()
                    }
                    onNewAnalysisClick()
                },
                onChatItemClick = { item ->
                    scope.launch {
                        drawerState.close()
                    }
                    onChatItemClick(item)
                },
                drawerWidth = drawerWidth
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("WhatsApp AI") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            MainContentArea(
                onNewAnalysisClick = {
                    scope.launch {
                        drawerState.close()
                    }
                    onNewAnalysisClick()
                },
                onUploadSuccess = { chatHistoryItem ->
                    navController.navigate("result/${chatHistoryItem.id}")
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun ChatHistoryDrawer(
    chatHistory: List<ChatHistoryItem>,
    isLoading: Boolean,
    onNewChatClick: () -> Unit,
    onChatItemClick: (ChatHistoryItem) -> Unit,
    drawerWidth: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // New Chat Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = onNewChatClick,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "New chat",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // History Section
        Text(
            text = "History",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (chatHistory.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No analysis history",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(chatHistory) { item ->
                    ChatHistoryItem(
                        item = item,
                        onClick = { onChatItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatHistoryItem(
    item: ChatHistoryItem,
    onClick: () -> Unit
) {
    Text(
        text = item.fileName.replace(".txt", "").takeIf { it.isNotEmpty() } ?: "Chat",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun MainContentArea(
    onNewAnalysisClick: () -> Unit,
    onUploadSuccess: (ChatHistoryItem) -> Unit,
    modifier: Modifier = Modifier,
    uploadViewModel: UploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    val uploadState by uploadViewModel.uploadState.collectAsState()
    val selectedFileName by uploadViewModel.selectedFileName.collectAsState()
    val selectedFileSize by uploadViewModel.selectedFileSize.collectAsState()

    var selectedUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shouldUpload by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    val filePickerLauncher = rememberFilePickerLauncher { uri ->
        if (uri != null) {
            selectedUri = uri
            val validationResult = FileUtils.validateFile(context, uri)
            when (validationResult) {
                is FileValidationResult.Success -> {
                    errorMessage = null
                    uploadViewModel.setSelectedFile(validationResult.fileName, validationResult.fileSize)
                }
                is FileValidationResult.Error -> {
                    errorMessage = validationResult.message
                    uploadViewModel.clearSelectedFile()
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

    // Handle file reading and upload
    LaunchedEffect(shouldUpload) {
        if (shouldUpload && selectedUri != null && selectedFileName != null && selectedFileSize != null) {
            shouldUpload = false
            val fileContent = FileUtils.readFileContent(context, selectedUri!!)
            if (fileContent != null) {
                uploadViewModel.uploadAndAnalyze(fileContent)
            } else {
                errorMessage = "Could not read file content"
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (screenWidth < 600.dp) 16.dp else 32.dp,
                    vertical = if (screenHeight < 800.dp) 16.dp else 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uploadState) {
                is UploadState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Analyzing...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    // File Selection Card - Responsive sizing
                    val cardWidth = if (screenWidth < 600.dp) {
                        Modifier.fillMaxWidth(0.9f)
                    } else {
                        Modifier.fillMaxWidth(0.7f)
                    }
                    
                    Card(
                        modifier = cardWidth.height(
                            if (screenHeight < 800.dp) 100.dp else 120.dp
                        ),
                        onClick = {
                            filePickerLauncher.launch("text/plain")
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = if (screenWidth < 600.dp) 16.dp else 24.dp,
                                    vertical = if (screenHeight < 800.dp) 16.dp else 24.dp
                                ),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    if (screenWidth < 600.dp) 28.dp else 32.dp
                                ),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(
                                if (screenWidth < 600.dp) 12.dp else 16.dp
                            ))
                            Text(
                                text = selectedFileName ?: "Choose a file (.txt only)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // File Info
                    if (selectedFileName != null && selectedFileSize != null) {
                        Text(
                            text = "File: $selectedFileName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Error Message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Bottom Message Input and FAB - Responsive positioning
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    horizontal = if (screenWidth < 600.dp) 12.dp else 16.dp,
                    vertical = if (screenHeight < 800.dp) 12.dp else 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Message Input
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (screenHeight < 800.dp) 48.dp else 56.dp),
                placeholder = {
                    Text(
                        text = "Message",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            // Voice input functionality can be added here
                        }
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice input",
                            modifier = Modifier.size(
                                if (screenWidth < 600.dp) 20.dp else 24.dp
                            ),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            // FAB
            FloatingActionButton(
                onClick = {
                    if (selectedFileName != null && selectedFileSize != null && selectedUri != null) {
                        shouldUpload = true
                    } else {
                        filePickerLauncher.launch("text/plain")
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .size(if (screenWidth < 600.dp) 48.dp else 56.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "New",
                    modifier = Modifier.size(
                        if (screenWidth < 600.dp) 20.dp else 24.dp
                    )
                )
            }
        }
    }
}
