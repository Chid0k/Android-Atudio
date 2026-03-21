package com.example.androidstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidstudio.ui.screens.*
import com.example.androidstudio.ui.theme.InterviewAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterviewAppTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    var loggedInUserId by remember { mutableStateOf<Int?>(null) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    loggedInUserId = userId
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToCreateProfile = { userId ->
                    loggedInUserId = userId
                    navController.navigate("create_profile/$userId") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable(
            route = "create_profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            CreateProfileScreen(
                userId = userId,
                onCreateSuccess = {
                    navController.navigate("home") {
                        popUpTo("create_profile/{userId}") { inclusive = true }
                    }
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onSendVerification = { navController.navigate("otp") }
            )
        }
        composable("otp") {
            OTPScreen(
                onNavigateBack = { navController.popBackStack() },
                onVerifyOTP = { navController.navigate("reset_password") }
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onConfirmReset = { navController.navigate("login") }
            )
        }
        composable("home") {
            HomeScreen(
                userId = loggedInUserId,
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToKnowledge = { navController.navigate("knowledge") }
            )
        }
        composable("knowledge") {
            KnowledgeScreen(
                userId = loggedInUserId,
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToArticle = { index -> navController.navigate("article_detail/$index") }
            )
        }
        composable(
            route = "article_detail/{articleIndex}",
            arguments = listOf(navArgument("articleIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val articleIndex = backStackEntry.arguments?.getInt("articleIndex")
            ArticleDetailScreen(
                userId = loggedInUserId,
                articleIndex = articleIndex,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("interview_setup") {
            InterviewSetupScreen(
                userId = loggedInUserId,
                onNavigateBack = { navController.popBackStack() },
                onStartInterview = { navController.navigate("interview") }
            )
        }
        composable("interview") {
            InterviewScreen(
                userId = loggedInUserId,
                onEndInterview = { navController.navigate("result") }
            )
        }
        composable("result") {
            ResultAnalysisScreen(
                onNavigateBack = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                } }
            )
        }
        composable("history") {
            HistoryScreen(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToKnowledge = { navController.navigate("knowledge") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToResult = { navController.navigate("result") }
            )
        }
        composable("profile") {
            ProfileScreen(
                userId = loggedInUserId,
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToKnowledge = { navController.navigate("knowledge") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToEdit = { navController.navigate("edit_profile") },
                onLogout = {
                    loggedInUserId = null
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("edit_profile") {
            EditProfileScreen(
                userId = loggedInUserId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
