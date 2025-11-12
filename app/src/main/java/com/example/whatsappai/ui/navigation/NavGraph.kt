package com.example.whatsappai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.ui.screen.chatlist.ChatListScreen
import com.example.whatsappai.ui.screen.result.ResultScreen
import com.example.whatsappai.ui.screen.upload.UploadScreen

sealed class Screen(val route: String) {
    object ChatList : Screen("chat_list")
    object Upload : Screen("upload")
    object Result : Screen("result/{chatHistoryId}") {
        fun createRoute(chatHistoryId: Long) = "result/$chatHistoryId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ChatList.route
    ) {
        composable(Screen.ChatList.route) {
            ChatListScreen(
                onNewAnalysisClick = {
                    // File picker will be opened in the main content area
                },
                onChatItemClick = { chatHistoryItem ->
                    navController.navigate(Screen.Result.createRoute(chatHistoryItem.id))
                },
                navController = navController
            )
        }

        composable(Screen.Upload.route) {
            UploadScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onUploadSuccess = { chatHistoryItem ->
                    navController.navigate(Screen.Result.createRoute(chatHistoryItem.id)) {
                        popUpTo(Screen.ChatList.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                androidx.navigation.navArgument("chatHistoryId") {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) { backStackEntry ->
            val chatHistoryId = backStackEntry.arguments?.getLong("chatHistoryId")
            ResultScreen(
                chatHistoryId = chatHistoryId,
                onBackClick = {
                    navController.popBackStack()
                },
                onDeleteClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

