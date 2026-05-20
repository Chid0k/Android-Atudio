package com.example.androidstudio.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.androidstudio.R
import com.example.androidstudio.interview.InterviewModule
import com.example.androidstudio.network.SessionManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
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

    var isHintVisible by remember { mutableStateOf(true) }
    var isQuestionVisible by remember { mutableStateOf(true) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onEndInterview) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isQuestionVisible = !isQuestionVisible }) {
                        Icon(
                            imageVector = if (isQuestionVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Question Visibility",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomControlButton(
                        icon = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        label = "MIC",
                        onClick = {
                            if (hasAudioPermission) {
                                if (isListening) {
                                    interviewModule?.stopListening()
                                } else {
                                    userSpeechText = ""
                                    interviewModule?.startListening()
                                }
                            } else {
                                launcher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        isActive = isListening
                    )

                    BottomControlButton(
                        icon = Icons.Default.Videocam,
                        label = "VIDEO",
                        onClick = { /* Mock */ }
                    )

                    BottomControlButton(
                        icon = Icons.Default.AutoAwesome,
                        label = "TIPS",
                        onClick = { isHintVisible = !isHintVisible },
                        isActive = isHintVisible,
                        activeColor = Color(0xFF007AFF)
                    )

                    BottomControlButton(
                        icon = Icons.Default.CallEnd,
                        label = "EXIT",
                        onClick = {
                            interviewModule?.analyzeAndSaveFeedback()
                            interviewModule?.disconnect()
                            onEndInterview()
                        },
                        containerColor = Color(0xFFE74C3C),
                        contentColor = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Background / Interviewer Avatar Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "AI Interviewer",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Question Card
                AnimatedVisibility(visible = isQuestionVisible) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1D2D).copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "CURRENT QUESTION",
                                    color = Color(0xFF3B82F6),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Surface(
                                    color = Color(0xFF1E3A34),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (isListening) Color(0xFF22C55E) else Color.Gray, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        val minutes = remainingTime / 60
                                        val seconds = remainingTime % 60
                                        Text(
                                            text = String.format("%02d:%02d", minutes, seconds),
                                            color = Color(0xFF22C55E),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = if (interviewerQuestion.startsWith("\"")) interviewerQuestion else "\"$interviewerQuestion\"",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // User Speech Bubble
                if (userSpeechText.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .padding(bottom = 16.dp, end = 60.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
                    ) {
                        Text(
                            text = "\"$userSpeechText\"",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.DarkGray
                        )
                    }
                }

                // Expert Insight Card
                AnimatedVisibility(visible = isHintVisible && interviewerHint.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = Color(0xFFE9F2FF),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "EXPERT INSIGHT",
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = { isHintVisible = false },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = Color.LightGray
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = interviewerHint,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomControlButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color = Color(0xFFE9EEF5),
    contentColor: Color = Color(0xFF374151),
    isActive: Boolean = false,
    activeColor: Color = Color(0xFF007AFF)
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(52.dp)
                .background(if (isActive) activeColor else containerColor, CircleShape)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isActive || containerColor != Color(0xFFE9EEF5)) Color.White else contentColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) activeColor else Color.Gray
        )
    }
}
