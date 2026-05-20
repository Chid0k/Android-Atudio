package com.example.androidstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import com.example.androidstudio.network.QuizCategory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidstudio.network.SessionManager
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
    var selectedQuizCategory by remember { mutableStateOf<QuizCategory?>(null) }
    var quizQuestionCount by remember { mutableIntStateOf(10) }
    var quizTimeLimit by remember { mutableStateOf<Int?>(5) }

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
                onNavigateToKnowledge = { navController.navigate("knowledge") },
                onNavigateToResult = { sessionId -> navController.navigate("result/$sessionId") }
            )
        }
        composable("knowledge") {
            KnowledgeScreen(
                userId = loggedInUserId,
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInterviewSetup = { navController.navigate("interview_setup") },
                onNavigateToArticle = { index -> navController.navigate("article_detail/$index") },
                onNavigateToQuizSetup = { category ->
                    selectedQuizCategory = category
                    navController.navigate("quiz_setup")
                }
            )
        }
        composable("quiz_setup") {
            selectedQuizCategory?.let { category ->
                QuizSetupScreen(
                    category = category,
                    onNavigateBack = { navController.popBackStack() },
                    onStartQuiz = { count, timeLimit ->
                        quizQuestionCount = count
                        quizTimeLimit = timeLimit
                        navController.navigate("quiz")
                    }
                )
            }
        }
        composable("quiz") {
            selectedQuizCategory?.let { category ->
                QuizScreen(
                    category = category,
                    questionCount = quizQuestionCount,
                    timeLimitMinutes = quizTimeLimit,
                    onQuizComplete = { score, total ->
                        // Show result screen
                        navController.popBackStack("home", inclusive = false)
                    },
                    onExit = { navController.popBackStack() }
                )
            }
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
                sessionId = SessionManager.sessionId,
                onEndInterview = { navController.navigate("result/${SessionManager.sessionId}?isFromInterview=true") }
            )
        }
        composable(
            route = "result/{sessionId}?isFromInterview={isFromInterview}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.IntType; defaultValue = -1 },
                navArgument("isFromInterview") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("sessionId")?.takeIf { it != -1 }
            val isFromInterview = backStackEntry.arguments?.getBoolean("isFromInterview") ?: false
            ResultAnalysisScreen(
                userId = loggedInUserId,
                sessionId = sessionId,
                isFromInterview = isFromInterview,
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
                onNavigateToResult = { sessionId -> navController.navigate("result/$sessionId") }
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
