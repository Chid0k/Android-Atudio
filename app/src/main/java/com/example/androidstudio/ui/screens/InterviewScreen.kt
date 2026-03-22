package com.example.androidstudio.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.androidstudio.interview.InterviewModule
import com.example.androidstudio.network.SessionManager
import kotlinx.coroutines.delay

@Composable
fun InterviewScreen(
    userId: Int?,
    sessionId: Int?,
    onEndInterview: () -> Unit
) {
    val context = LocalContext.current
    var interviewerQuestion by remember { mutableStateOf("Đang kết nối tới người phỏng vấn...") }
    var interviewerHint by remember { mutableStateOf("") }
    var userSpeechText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isQuestionVisible by remember { mutableStateOf(true) }
    var isHintVisible by remember { mutableStateOf(true) }

    // Countdown Timer State
    var remainingTime by remember { 
        mutableStateOf(SessionManager.selectedDurationMinutes * 60) 
    }

    val interviewModule = remember {
        if (userId != null && sessionId != null) {
            InterviewModule(
                context = context,
                userId = userId,
                sessionId = sessionId,
                onResponseReceived = { question, hint ->
                    interviewerQuestion = question
                    interviewerHint = hint
                    isListening = false
                },
                onUserSpeechRecognized = { speech ->
                    userSpeechText = speech
                },
                onListeningStateChanged = { listening ->
                    isListening = listening
                },
                onError = { error ->
                    errorMessage = error
                    isListening = false
                }
            )
        } else {
            null
        }
    }

    // Timer logic
    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000L)
            remainingTime--
        } else {
            interviewModule?.analyzeAndSaveFeedback()
            interviewModule?.disconnect()
            onEndInterview()
        }
    }

    // Camera/Mic permissions
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
        interviewModule?.connect()
    }

    DisposableEffect(Unit) {
        onDispose {
            interviewModule?.disconnect()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Placeholder for Interviewer Video
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            Text("Interviewer Video", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        // Top Row for Timer and Recording Indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Timer Display
            val minutes = remainingTime / 60
            val seconds = remainingTime % 60
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    color = if (remainingTime < 60) Color.Red else Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Recording Indicator
            if (isListening) {
                Row(
                    modifier = Modifier
                        .background(Color.Green.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Đang nghe...", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Top Info Area (Questions & Hints)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .width(280.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Interviewer Question Card
            AnimatedVisibility(visible = isQuestionVisible) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Người phỏng vấn AI", fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(
                            interviewerQuestion,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if (errorMessage != null) {
                            Text(
                                errorMessage!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Hint Card
            AnimatedVisibility(visible = isHintVisible && interviewerHint.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Yellow.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Gợi ý", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                            Text(
                                interviewerHint,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Sidebar Toggles (Question & Hint)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Toggle Question
            IconButton(
                onClick = { isQuestionVisible = !isQuestionVisible },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isQuestionVisible) Color.Blue.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(Icons.Default.Help, contentDescription = "Ẩn/Hiện câu hỏi", tint = Color.White)
            }

            // Toggle Hint
            IconButton(
                onClick = { isHintVisible = !isHintVisible },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isHintVisible) Color.Yellow.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = "Ẩn/Hiện gợi ý", tint = Color.White)
            }
        }

        // User PIP (Picture-in-Picture) with Speech Text
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp)
                .size(150.dp, 200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                if (userSpeechText.isNotEmpty()) {
                    Text(
                        userSpeechText,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Bạn", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic toggle
            IconButton(
                onClick = {
                    if (hasAudioPermission) {
                        if (isListening) {
                            interviewModule?.stopListening()
                        } else {
                            userSpeechText = "" // Reset text khi bắt đầu nghe mới
                            interviewModule?.startListening()
                        }
                    } else {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isListening) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            // End call
            IconButton(
                onClick = {
                    interviewModule?.analyzeAndSaveFeedback()
                    interviewModule?.disconnect()
                    onEndInterview()
                },
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Red, CircleShape)
            ) {
                Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }

            // Camera toggle (Mock)
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
            }
        }
    }
}
