package com.example.androidstudio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.ProfileUpdateRequest
import com.example.androidstudio.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: Int?,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            isLoading = true
            try {
                val profile = RetrofitClient.apiService.getUserProfile(userId)
                name = profile.full_name ?: ""
                major = profile.major ?: ""
                experience = profile.experience ?: ""
                skills = profile.skills ?: ""
            } catch (e: Exception) {
                errorMessage = "Không thể tải thông tin: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0D3B34))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EditField(label = "Họ và tên", value = name, onValueChange = { name = it })
                Spacer(modifier = Modifier.height(16.dp))
                
                EditField(label = "Chuyên ngành", value = major, onValueChange = { major = it })
                Spacer(modifier = Modifier.height(16.dp))
                
                EditField(label = "Kinh nghiệm", value = experience, onValueChange = { experience = it })
                Spacer(modifier = Modifier.height(16.dp))

                EditField(label = "Kỹ năng", value = skills, onValueChange = { skills = it })
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (userId != null) {
                            scope.launch {
                                isSaving = true
                                errorMessage = null
                                try {
                                    val request = ProfileUpdateRequest(
                                        user_id = userId,
                                        full_name = name,
                                        major = major,
                                        experience = experience,
                                        skills = skills
                                    )
                                    RetrofitClient.apiService.updateProfile(userId, request)
                                    onNavigateBack()
                                } catch (e: Exception) {
                                    errorMessage = "Cập nhật thất bại: ${e.localizedMessage}"
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D3B34)
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0D3B34),
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}
