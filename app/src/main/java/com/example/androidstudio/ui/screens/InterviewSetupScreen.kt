package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.InterviewSessionRequest
import com.example.androidstudio.network.RetrofitClient
import com.example.androidstudio.network.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSetupScreen(
    userId: Int?,
    onNavigateBack: () -> Unit,
    onStartInterview: () -> Unit
) {
    var jobTitle by remember { mutableStateOf("Junior Pentester") }
    var jobDescription by remember { mutableStateOf("Kiểm thử xâm nhập ứng dụng web") }
    
    val trainingModes = listOf("Tự do", "Cơ bản", "Nâng cao")
    var selectedMode by remember { mutableStateOf(trainingModes[0]) }

    val durations = listOf("3 Phút", "5 Phút", "10 Phút", "15 Phút", "30 Phút", "45 Phút", "60 Phút")
    var selectedDuration by remember { mutableStateOf(durations[1]) }
    var durationExpanded by remember { mutableStateOf(false) }

    val difficulties = listOf("Dễ", "Trung bình", "Khó")
    var selectedDifficulty by remember { mutableStateOf(difficulties[1]) }
    var difficultyExpanded by remember { mutableStateOf(false) }

    var selectedLanguage by remember { mutableStateOf("Tiếng Việt") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val primaryColor = Color(0xFF0D3B34)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết lập phỏng vấn", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAF9))
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Thông tin công việc", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Tên công việc") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = jobDescription,
                onValueChange = { jobDescription = it },
                label = { Text("Mô tả công việc") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(28.dp))

            Text("Cấu hình phỏng vấn", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("Chế độ luyện tập", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                trainingModes.forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        label = { Text(mode) },
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text("Thời lượng", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth().clickable { durationExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedDuration, modifier = Modifier.weight(1.0f), fontSize = 14.sp)
                                Icon(Icons.Default.KeyboardArrowDown, null, tint = primaryColor)
                            }
                        }
                        DropdownMenu(
                            expanded = durationExpanded,
                            onDismissRequest = { durationExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            durations.forEach { duration ->
                                DropdownMenuItem(
                                    text = { Text(duration) },
                                    onClick = {
                                        selectedDuration = duration
                                        durationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1.0f)) {
                    Text("Độ khó", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth().clickable { difficultyExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedDifficulty, modifier = Modifier.weight(1.0f), fontSize = 14.sp)
                                Icon(Icons.Default.KeyboardArrowDown, null, tint = primaryColor)
                            }
                        }
                        DropdownMenu(
                            expanded = difficultyExpanded,
                            onDismissRequest = { difficultyExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            difficulties.forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level) },
                                    onClick = {
                                        selectedDifficulty = level
                                        difficultyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Text("Ngôn ngữ", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedLanguage == "Tiếng Việt",
                    onClick = { selectedLanguage = "Tiếng Việt" },
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                )
                Text("Tiếng Việt", fontSize = 14.sp, modifier = Modifier.clickable { selectedLanguage = "Tiếng Việt" })
                Spacer(modifier = Modifier.width(24.dp))
                RadioButton(
                    selected = selectedLanguage == "Tiếng Anh",
                    onClick = { selectedLanguage = "Tiếng Anh" },
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                )
                Text("Tiếng Anh", fontSize = 14.sp, modifier = Modifier.clickable { selectedLanguage = "Tiếng Anh" })
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (userId == null) {
                        errorMessage = "Lỗi: Không tìm thấy ID người dùng"
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val durationValue = selectedDuration.split(" ")[0].toIntOrNull() ?: 30
                            SessionManager.selectedDurationMinutes = durationValue
                            SessionManager.jobTitle = jobTitle
                            SessionManager.jobDescription = jobDescription
                            SessionManager.language = selectedLanguage

                            val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                            
                            val response = RetrofitClient.apiService.createInterviewSession(
                                InterviewSessionRequest(
                                    userId = userId,
                                    title = jobTitle,
                                    interviewType = "Job Interview",
                                    difficulty = selectedDifficulty,
                                    mode = selectedMode,
                                    durationMinutes = durationValue,
                                    startTime = currentTime,
                                    status = "waited",
                                    configJson = jobDescription
                                )
                            )
                            SessionManager.sessionId = response.sessionId
                            onStartInterview()
                        } catch (e: Exception) {
                            errorMessage = "Lỗi khởi tạo phỏng vấn: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Bắt đầu phỏng vấn", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
