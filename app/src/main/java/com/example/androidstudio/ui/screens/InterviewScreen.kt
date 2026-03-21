package com.example.androidstudio.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
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
import com.example.androidstudio.network.InterviewSessionUpdateRequest
import com.example.androidstudio.network.RetrofitClient
import com.example.androidstudio.network.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InterviewScreen(
    userId: Int?,
    onEndInterview: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interviewModule = remember(userId) { InterviewModule(context, userId) }
    val sttText by interviewModule.sttText.collectAsState()
    val isListening by interviewModule.isListening.collectAsState()
    
    var isEnding by remember { mutableStateOf(false) }
    val startTime = remember { Date() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            interviewModule.startListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            interviewModule.destroy()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Placeholder for Interviewer Video
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            Text("Interviewer Video", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        // Recording Indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("REC 00:04:11", color = Color.White, fontSize = 12.sp)
        }

        // Interviewer Question Card (Realtime streaming text)
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .width(280.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Harriet M.", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    text = if (sttText.isEmpty()) "Waiting for response..." else sttText,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // User PIP (Picture-in-Picture)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp)
                .size(100.dp, 140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
        ) {
            Text("User", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        // Controls
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
                    if (isListening) {
                        interviewModule.stopListening()
                    } else {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            interviewModule.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isListening) Color.Red else Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            // End call
            IconButton(
                onClick = {
                    val sessionId = SessionManager.sessionId
                    if (sessionId != null) {
                        scope.launch {
                            isEnding = true
                            try {
                                val endTime = Date()
                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                val actualDuration = ((endTime.time - startTime.time) / 60000).toInt()
                                
                                RetrofitClient.apiService.updateInterviewSession(
                                    sessionId = sessionId,
                                    request = InterviewSessionUpdateRequest(
                                        actualDuration = actualDuration,
                                        startTime = sdf.format(startTime),
                                        endTime = sdf.format(endTime),
                                        score = 85,
                                        status = "completed",
                                        feedbackJson = "Phỏng vấn tốt, kỹ năng giao tiếp ổn định.",
                                        isFavorite = 1
                                    )
                                )
                                onEndInterview()
                            } catch (e: Exception) {
                                onEndInterview()
                            } finally {
                                isEnding = false
                            }
                        }
                    } else {
                        onEndInterview()
                    }
                },
                enabled = !isEnding,
                modifier = Modifier
                    .size(64.dp)
                    .background(if (isEnding) Color.Gray else Color.Red, CircleShape)
            ) {
                if (isEnding) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }

            // Camera toggle
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
