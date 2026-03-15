package com.example.androidstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToKnowledge = { navController.navigate("knowledge") }
            )
        }
        composable("knowledge") {
            KnowledgeScreen(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToArticle = { navController.navigate("article_detail") }
            )
        }
        composable("article_detail") {
            ArticleDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("interview_setup") {
            InterviewSetupScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartInterview = { navController.navigate("interview") }
            )
        }
        composable("interview") {
            InterviewScreen(
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
                onNavigateToEdit = { navController.navigate("edit_profile") }
            )
        }
        composable("edit_profile") {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
