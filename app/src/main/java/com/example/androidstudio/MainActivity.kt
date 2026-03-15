package com.example.androidstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { 
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { /* Navigate to register */ }
            )
        }
        composable("home") { 
            HomeScreen(
                onNavigateToProfile = { navController.navigate("profile") }
            ) 
        }
        composable("profile") { 
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
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
